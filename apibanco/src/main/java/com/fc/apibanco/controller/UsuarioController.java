package com.fc.apibanco.controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fc.apibanco.dto.UsuarioDTO;
import com.fc.apibanco.dto.UsuarioRequest;
import com.fc.apibanco.model.PasswordEncriptada;
import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.UsuarioRepository;
import com.fc.apibanco.service.UsuarioService;
import com.fc.apibanco.util.AESUtil;
import com.fc.apibanco.util.Constantes;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;  
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioService = usuarioService;
    }
    
    //---------------------------CREAR USUARIOS------------------------------------------------------------------------------------
    @PostMapping("/usuarios")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioRequest request,
                                          @AuthenticationPrincipal UserDetails userDetails) {

        usuarioRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO));

        Optional<Usuario> existenteOpt = usuarioRepository.findByEmail(request.getEmail());
        Usuario usuario;

        if (existenteOpt.isPresent()) {
            usuario = existenteOpt.get();
            if (usuario.getPasswordHash() != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe con contraseña");
            }
        } else {
            usuario = new Usuario();
            usuario.setEmail(request.getEmail());
        }

        usuario.setUsername(request.getUsername());
        usuario.setFirstName(request.getFirstName());
        usuario.setLastName(request.getLastName());
        usuario.setRol(request.getRol());
        usuario.setActivo(request.isActivo());
        usuario.setTeam(request.getTeam());
        usuario.setDepartment(request.getDepartment());

        if(Constantes.USER.equalsIgnoreCase(request.getRol())) {
            if (request.getSupervisorId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un usuario debe tener un supervisor asignado");
            }
        }
        
        if (request.getSupervisorId() != null) {
            Usuario supervisor = usuarioRepository.findById(request.getSupervisorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supervisor no encontrado"));
            
            if (supervisor.getId() !=null && supervisor.getId().equals(usuario.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un supervisor no puede ser su propio supervisor");
            }
            if (supervisor.getEmail().equalsIgnoreCase(request.getEmail())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un supervisor no puede ser su propio supervisor");
            }
            usuario.setSupervisor(supervisor);
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        PasswordEncriptada pass = new PasswordEncriptada();
        pass.setUsuario(usuario);
        pass.setHash(AESUtil.encrypt(request.getPassword()));
        usuario.setPasswordEncriptada(pass);

        usuarioRepository.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado/actualizado correctamente");
    }
    
    //------------------------ACTUALIZAR USUARIOS--------------------------------------------------------------------------------
    @PutMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id,
                                               @RequestBody UsuarioRequest usuarioActualizado) {
        return usuarioRepository.findById(id)
            .map(usuario -> {
                usuario.setUsername(usuarioActualizado.getUsername());
                usuario.setFirstName(usuarioActualizado.getFirstName());
                usuario.setLastName(usuarioActualizado.getLastName());
                usuario.setRol(usuarioActualizado.getRol());
                usuario.setActivo(usuarioActualizado.isActivo());
                usuario.setTeam(usuarioActualizado.getTeam());
                usuario.setDepartment(usuarioActualizado.getDepartment());

                if (usuarioActualizado.getSupervisorId() != null) {
                    Usuario supervisor = usuarioRepository.findById(usuarioActualizado.getSupervisorId())
                        .orElse(null);
                    usuario.setSupervisor(supervisor);
                }

                if (usuarioActualizado.getPassword() != null &&
                    !usuarioActualizado.getPassword().isBlank()) {
                    String passwordOriginal = usuarioActualizado.getPassword();

                    usuario.setPasswordHash(passwordEncoder.encode(passwordOriginal));

                    PasswordEncriptada pass = new PasswordEncriptada();
                    pass.setUsuario(usuario);
                    pass.setHash(AESUtil.encrypt(passwordOriginal));
                    usuario.setPasswordEncriptada(pass);
                }

                usuarioRepository.save(usuario);
                return ResponseEntity.ok("✅ Usuario actualizado correctamente");
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                                           .body("❌ Usuario no encontrado"));
    }

    //------------------------------LISTAR USUARIOS SIN PASSWORD----------------------------------------------
    @GetMapping("/usuarios/sin-password")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<UsuarioDTO>> listarUsuariosSinPassword(@AuthenticationPrincipal UserDetails userDetails) {
        usuarioRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO));

        List<UsuarioDTO> usuariosSinPassword = usuarioRepository.findAll().stream()
            .filter(u -> u.getPasswordHash() == null || u.getPasswordHash().isBlank())
            .map(u -> new UsuarioDTO(
                u.getId(),
                u.getUsername(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getRol(),
                u.isActivo(),
                u.getTeam(),
                u.getDepartment(),
                u.getSupervisor() != null ? u.getSupervisor().getId() : null,
                u.getSupervisor() != null ? u.getSupervisor().getFirstName() + " " + u.getSupervisor().getLastName() : null,
                null
            ))
            .toList();

        return ResponseEntity.ok(usuariosSinPassword);
    }

    //--------------------END POINT TEAMS-----------------------------------------------------------------------------
    @GetMapping("/teams")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public List<String> listarTeams() {
        return usuarioRepository.findAll().stream()
            .map(Usuario::getTeam)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    }
    
    //--------------------END POINT DEPARTAMENTOS------------------------------------------------------------------------------
    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public List<String> listarDepartments() {
        return usuarioRepository.findAll().stream()
            .map(Usuario::getDepartment)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
    }
    
    //---------------END POINT SUPERVISORES-------------------------------------------------------------------------------------
    @GetMapping("/supervisores")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public List<UsuarioDTO> listarSupervisores() {
        return usuarioRepository.findAll().stream()
            .filter(u -> Constantes.SUPERVISOR.equals(u.getRol()))
            .map(u -> new UsuarioDTO(
                u.getId(),
                u.getUsername(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getRol(),
                u.isActivo(),
                u.getTeam(),
                u.getDepartment(),
                null, null, null
            ))
            .toList();
    }
    
    //--------------LISTAR CORREOS DE USUARIO-----------------------------------------------------------------------------------
    @GetMapping("/usuarios/correos")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<String>> listarCorreos(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String filtro) {

        usuarioRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO));

        List<String> correos = usuarioRepository.findAll().stream()
            .map(Usuario::getEmail)
            .filter(email -> email != null && !email.isBlank())
            .filter(email -> filtro == null || email.toLowerCase().contains(filtro.toLowerCase()))
            .toList();

        return ResponseEntity.ok(correos);
    }
    
    //--------------BORRADO LOGICO USUARIOS-----------------------------------------------------------------------------------
    @DeleteMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> borrarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return ResponseEntity.noContent().build();
    }

}
