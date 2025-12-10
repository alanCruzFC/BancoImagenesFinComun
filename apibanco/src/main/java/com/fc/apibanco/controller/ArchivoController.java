package com.fc.apibanco.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fc.apibanco.dto.ArchivoDTO;
import com.fc.apibanco.dto.RegistroDTO;
import com.fc.apibanco.dto.UsuarioDTO;
import com.fc.apibanco.model.CorreoAutorizado;
import com.fc.apibanco.model.Metadata;
import com.fc.apibanco.model.PasswordEncriptada;
import com.fc.apibanco.model.Registro;
import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.CorreoAutorizadoRepository;
import com.fc.apibanco.repository.MetadataRepository;
import com.fc.apibanco.repository.RegistroRepository;
import com.fc.apibanco.repository.UsuarioRepository;
import com.fc.apibanco.util.AESUtil;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class ArchivoController {

	@Autowired
    private MetadataRepository metadataRepository;
	
	@Autowired
	private RegistroRepository registroRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private CorreoAutorizadoRepository correoAutorizadoRepository;

    ArchivoController(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }
    
//------------------------VIZUALIZAR LAS IMAGENES -----------------------------------------------
    
	@GetMapping("/visualizar/{numeroSolicitud}")
	public ResponseEntity<?> visualizar(@PathVariable String numeroSolicitud,
	                                    @AuthenticationPrincipal UserDetails userDetails) throws IOException {

	    Registro registro = registroRepository.findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

	    String rol = userDetails.getAuthorities().iterator().next().getAuthority();
	    String correoUsuario = userDetails.getUsername();
	    
	    System.out.println("Usuario: " + correoUsuario);
	    System.out.println("Rol: " + rol);
	    System.out.println("Creador: " + registro.getCreador().getUsername());
	    System.out.println("Correos autorizados: " + registro.getCorreosAutorizados());


	    Path carpeta = Paths.get("Archivos", numeroSolicitud);
	    if (!Files.exists(carpeta)) {
	    	return ResponseEntity.ok(Collections.emptyList());
	    }

	    List<ArchivoDTO> archivos = Files.list(carpeta)
	        .filter(Files::isRegularFile)
	        .map(path -> new ArchivoDTO(path.getFileName().toString(), "/descargar/" + numeroSolicitud + "/" + path.getFileName()))
	        .toList();

	    return ResponseEntity.ok(archivos);
	}
	
//----------------------CARGAR IMAGENES AL SERVIDOR Y METADATA------------------------------------	
	
	@PostMapping("/subir/{numeroSolicitud}")
	public ResponseEntity<?> subirImagen(@PathVariable String numeroSolicitud,
	                                     @RequestParam("tipo") String tipo,
	                                     @RequestParam("archivo") MultipartFile archivo,
	                                     @AuthenticationPrincipal UserDetails userDetails,
	                                     HttpServletRequest request) throws IOException {

	    Registro registro = registroRepository.findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));

	    Usuario usuario;
	    if (userDetails != null) {
	        usuario = usuarioRepository.findByUsername(userDetails.getUsername())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

	        String rol = usuario.getRol();
	        boolean accesoPermitido = rol.equals("ADMIN") ||
	                registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername());

	        if (!accesoPermitido) {
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
	        }
	    } else {
	        String consumidor = (String) request.getAttribute("consumidor");
	        if (consumidor == null) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API key inválida");
	        }

	        usuario = usuarioRepository.findByUsername(consumidor)
	            .orElseGet(() -> {
	                Usuario nuevo = new Usuario();
	                nuevo.setUsername(consumidor);
	                nuevo.setEmail(consumidor);
	                nuevo.setRol("USER");
	                nuevo.setActivo(true);
	                return usuarioRepository.save(nuevo);
	            });
	    }

	    numeroSolicitud = numeroSolicitud.trim();
	    Path carpeta = Paths.get("Archivos", numeroSolicitud);
	    Files.createDirectories(carpeta);

	    String extension = FilenameUtils.getExtension(archivo.getOriginalFilename());
	    String nombreFinal = tipo + "_" + numeroSolicitud + "." + extension;
	    Path destino = carpeta.resolve(nombreFinal);

	    Metadata existente = metadataRepository.findByRegistroAndTipoDocumento(registro, tipo);

	    if (existente != null) {
	    	Path archivoAnterior = carpeta.resolve(existente.getNombreArchivo());
	        Files.deleteIfExists(archivoAnterior);
	        existente.setNombreArchivo(nombreFinal);
	        existente.setFechaSubida(LocalDateTime.now());
	        existente.setSubidoPor(usuario);
	        metadataRepository.save(existente);
	    } else {
	        Metadata metadata = new Metadata();
	        metadata.setNombreArchivo(nombreFinal);
	        metadata.setTipoDocumento(tipo);
	        metadata.setFechaSubida(LocalDateTime.now());
	        metadata.setRegistro(registro);
	        metadata.setSubidoPor(usuario);
	        metadataRepository.save(metadata);
	    }



	    Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

	    ArchivoDTO dto = new ArchivoDTO(nombreFinal, "/api/descargar/" + numeroSolicitud + "/" + nombreFinal);
	    return ResponseEntity.ok(Map.of("mensaje", "Archivo subido correctamente", "archivo", dto));
	}
	
	//-----------------------CARGAR MULTIPLES IMAGENES AL MISMO TIEMPO------------------------------------
	
	@PostMapping("/subir-multiple/{numeroSolicitud}")
	public ResponseEntity<?> subirDocumentos(@PathVariable String numeroSolicitud,
	                                         @RequestParam("archivos") List<MultipartFile> archivos,
	                                         @RequestParam("tipos") List<String> tipos,
	                                         @AuthenticationPrincipal UserDetails userDetails,
	                                         HttpServletRequest request) throws IOException {

	    Registro registro = registroRepository.findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));

	    Usuario usuario;

	    if (userDetails != null) {
	        usuario = usuarioRepository.findByUsername(userDetails.getUsername())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

	        String rol = usuario.getRol();
	        boolean accesoPermitido = rol.equals("ADMIN") ||
	                registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername());

	        if (!accesoPermitido) {
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
	        }
	    } else {
	        String consumidor = (String) request.getAttribute("consumidor");
	        if (consumidor == null) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API key inválida");
	        }

	        usuario = registro.getCreador();
	        if (usuario == null) {
	            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El registro no tiene creador definido");
	        }
	    }

	    numeroSolicitud = numeroSolicitud.trim();
	    Path carpeta = Paths.get("Archivos", numeroSolicitud);
	    Files.createDirectories(carpeta);

	    List<ArchivoDTO> archivosSubidos = new ArrayList<>();

	    for (int i = 0; i < archivos.size(); i++) {
	        MultipartFile archivo = archivos.get(i);
	        String tipo = tipos.get(i);

	        String extension = FilenameUtils.getExtension(archivo.getOriginalFilename());
	        String nombreFinal = tipo + "_" + numeroSolicitud + "." + extension;
	        Path destino = carpeta.resolve(nombreFinal);

	        Metadata existente = metadataRepository.findByRegistroAndTipoDocumento(registro, tipo);

	        if (existente != null) {
	        	Path archivoAnterior = carpeta.resolve(existente.getNombreArchivo());
	            Files.deleteIfExists(archivoAnterior);
	            existente.setNombreArchivo(nombreFinal);
	            existente.setFechaSubida(LocalDateTime.now());
	            existente.setSubidoPor(usuario);
	            metadataRepository.save(existente);
	        } else {
	            Metadata metadata = new Metadata();
	            metadata.setNombreArchivo(nombreFinal);
	            metadata.setTipoDocumento(tipo);
	            metadata.setFechaSubida(LocalDateTime.now());
	            metadata.setRegistro(registro);
	            metadata.setSubidoPor(usuario);
	            metadataRepository.save(metadata);
	        }



	        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

	        archivosSubidos.add(new ArchivoDTO(nombreFinal, "/api/descargar/" + numeroSolicitud + "/" + nombreFinal));
	    }

	    return ResponseEntity.ok(Map.of("mensaje", "Archivos subidos correctamente", "archivos", archivosSubidos));
	}
	
	//-----------------------LISTAR REGISTROS-------------------------------------------------------------
	
	@GetMapping("/registros")
	public ResponseEntity<List<RegistroDTO>> obtenerRegistros(@AuthenticationPrincipal UserDetails userDetails) {

	    Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

	    String rol = usuario.getRol();
	    String correoUsuario = usuario.getEmail();

	    List<Registro> registros;

	    if (rol.equals("ADMIN")) {
	        registros = registroRepository.findByFechaEliminacionIsNull();
	    } else if (rol.equals("USER")) {
	        registros = registroRepository.findByFechaEliminacionIsNull().stream()
	            .filter(registro ->
	                (registro.getCreador() != null && registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername())) ||
	                registro.getCorreosAutorizados().stream()
	                    .map(CorreoAutorizado::getCorreo)
	                    .anyMatch(correo -> correo.equalsIgnoreCase(correoUsuario)))
	            .toList();
	    } else if (rol.equals("SUPERVISOR")) {
	        registros = registroRepository.findByFechaEliminacionIsNull().stream()
	                .filter(registro ->
	                    (registro.getCreador() != null && registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername())) ||
	                    (registro.getCreador() != null && registro.getCreador().getSupervisor() != null &&
	                     registro.getCreador().getSupervisor().getId().equals(usuario.getId())))
	                .toList();
	    } else {
	        registros = registroRepository.findByFechaEliminacionIsNull().stream()
	            .filter(registro ->
	                registro.getCorreosAutorizados().stream()
	                    .map(CorreoAutorizado::getCorreo)
	                    .anyMatch(correo -> correo.equalsIgnoreCase(correoUsuario)))
	            .toList();
	    }

	    List<RegistroDTO> respuesta = registros.stream()
	        .map(registro -> {
	            List<String> correos = correoAutorizadoRepository.findByRegistroId(registro.getId())
	                .stream()
	                .map(CorreoAutorizado::getCorreo)
	                .filter(c -> !c.equalsIgnoreCase(registro.getCreador().getEmail()))
	                .toList();

	            return new RegistroDTO(
	                registro.getNumeroSolicitud(),
	                registro.getCarpetaRuta(),
	                registro.getCreador() != null ? registro.getCreador().getUsername() : "Usuario Desconocido",
	                registro.getFechaCreacion(),
	                correos
	            );
	        })
	        .toList();

	    return ResponseEntity.ok(respuesta);
	}
	
//--------------------------OBTENER REGISTRO POR NUMERO DE SOLICITUD-------------------------------------------------------------------------------------
	
	@GetMapping("/registros/{numeroSolicitud}")
	public ResponseEntity<RegistroDTO> obtenerRegistro(
	        @PathVariable String numeroSolicitud,
	        @AuthenticationPrincipal UserDetails userDetails) throws IOException {

	    Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

	    Registro registro = registroRepository.findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));

	    List<String> correos = correoAutorizadoRepository.findByRegistroId(registro.getId())
	        .stream()
	        .map(CorreoAutorizado::getCorreo)
	        .filter(c -> !c.equalsIgnoreCase(registro.getCreador().getEmail()))
	        .toList();

	    Path carpeta = Paths.get("Archivos", numeroSolicitud);
	    List<ArchivoDTO> archivos = Files.exists(carpeta)
	        ? Files.list(carpeta)
	            .filter(Files::isRegularFile)
	            .map(path -> new ArchivoDTO(path.getFileName().toString(),
	                                        "/api/descargar/" + numeroSolicitud + "/" + path.getFileName()))
	            .toList()
	        : Collections.emptyList();

	    boolean esDueño = usuario.getRol().equals("ADMIN") ||
	                      (registro.getCreador() != null &&
	                       registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername()));

	    RegistroDTO dto = new RegistroDTO(
	        registro.getNumeroSolicitud(),
	        registro.getCarpetaRuta(),
	        registro.getCreador() != null ? registro.getCreador().getUsername() : "Usuario Desconocido",
	        registro.getFechaCreacion(),
	        correos
	    );
	    dto.setImagenes(archivos);
	    dto.setEsDueño(esDueño);

	    return ResponseEntity.ok(dto);
	}

	
	//----------------------LISTAR USUARIOS Y MOSTRAR CONTRASEÑA -----------------------------------------------
	
	@GetMapping("/usuarios")
	public ResponseEntity<List<UsuarioDTO>> listarUsuarios(@AuthenticationPrincipal UserDetails userDetails) {

	    Usuario admin = usuarioRepository.findByUsername(userDetails.getUsername())
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no válido"));

	    if (!"ADMIN".equals(admin.getRol())) {
	        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
	    }

	    List<Usuario> usuarios = usuarioRepository.findAll();

	    List<UsuarioDTO> respuesta = usuarios.stream()
	        .map(usuario -> {
	            String desencriptada = null;
	            PasswordEncriptada pass = usuario.getPasswordEncriptada();
	            if (pass != null && pass.getHash() != null) {
	                try {
	                    desencriptada = desencriptar(pass.getHash());
	                } catch (Exception e) {
	                    desencriptada = "[Error al desencriptar]";
	                }
	            }

	            return new UsuarioDTO(
	                usuario.getId(),
	                usuario.getUsername(),
	                usuario.getFirstName(),
	                usuario.getLastName(),
	                usuario.getEmail(),
	                usuario.getRol(),
	                usuario.isActivo(),
	                usuario.getTeam(),
	                usuario.getDepartment(),
	                usuario.getSupervisor() != null ? usuario.getSupervisor().getId() : null,
	                usuario.getSupervisor() != null ? usuario.getSupervisor().getFirstName() + " " + usuario.getSupervisor().getLastName() : null,
	                desencriptada
	            );
	        })
	        .toList();

	    return ResponseEntity.ok(respuesta);
	}

	private String desencriptar(String hash) {
	    return AESUtil.decrypt(hash);
	}

	
//	----------------------CARGAR IMAGENES PARA VISUALIZAR------------------------------------------

	@GetMapping("/descargar/{numeroSolicitud}/{nombreArchivo}")
	public ResponseEntity<Resource> descargarArchivo(
	        @PathVariable String numeroSolicitud,
	        @PathVariable String nombreArchivo,
	        @RequestParam(defaultValue = "false") boolean inline) throws IOException {

	    Path ruta = Paths.get("Archivos", numeroSolicitud).resolve(nombreArchivo).normalize();
	    Resource recurso = new UrlResource(ruta.toUri());

	    if (!recurso.exists()) {
	        return ResponseEntity.notFound().build();
	    }

	    String contentType = Files.probeContentType(ruta);
	    if (contentType == null) {
	        contentType = "application/octet-stream";
	    }

	    String disposition = inline
	            ? "inline; filename=\"" + nombreArchivo + "\""
	            : "attachment; filename=\"" + nombreArchivo + "\"";

	    return ResponseEntity.ok()
	            .contentType(MediaType.parseMediaType(contentType))
	            .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
	            .body(recurso);
	}
}
