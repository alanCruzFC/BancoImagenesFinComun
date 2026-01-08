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
import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.fc.apibanco.dto.TipoDocumentoStatusDTO;
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
import com.fc.apibanco.util.Constantes;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class ArchivoController {

	private final MetadataRepository metadataRepository;
	private final RegistroRepository registroRepository;
	private final UsuarioRepository usuarioRepository;
	private final CorreoAutorizadoRepository correoAutorizadoRepository;

    public ArchivoController(MetadataRepository metadataRepository, RegistroRepository registroRepository, UsuarioRepository usuarioRepository, CorreoAutorizadoRepository correoAutorizadoRepository) {
        this.metadataRepository = metadataRepository;
        this.registroRepository = registroRepository;
        this.usuarioRepository = usuarioRepository;
        this.correoAutorizadoRepository = correoAutorizadoRepository;
    }
	
//----------------------CARGAR IMAGENES AL SERVIDOR Y METADATA------------------------------------	
	
    @PostMapping("/subir/{numeroSolicitud}") 
    @PreAuthorize("hasRole('SUPERADMIN')") 
    public ResponseEntity<Map<String, Object>> subirImagen(
            @PathVariable String numeroSolicitud, 
            @RequestParam("tipo") String tipo, 
            @RequestParam("archivo") MultipartFile archivo, 
            @AuthenticationPrincipal UserDetails userDetails, 
            HttpServletRequest request) throws IOException {

        // ---------------- VALIDAR REGISTRO ----------------
        Registro registro = registroRepository.findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constantes.NOT_FOUND));

        // ---------------- OBTENER USUARIO AUTENTICADO ----------------
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debe estar autenticado desde la vista");
        }
        Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO));

        numeroSolicitud = numeroSolicitud.trim();
        Path carpeta = Paths.get(Constantes.ARCHIVOS_CARP, numeroSolicitud);
        Files.createDirectories(carpeta);

        // ---------------- TIPOS FIJOS ----------------
        Set<String> tiposfijos = Constantes.TIPOS_FIJOS;

        String tipoNormalizado = tipo.trim().toUpperCase();

        // ---------------- VALIDACIÓN DE TIPO ----------------
        if (tiposfijos.contains(tipoNormalizado)) {
        	// No hacemos nada porque ya es un tipo fijo válido
        } else {
            String tipoExtra = tipo.trim();

            for (String fijo : tiposfijos) {
                if (tipoExtra.toUpperCase().startsWith(fijo)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(Constantes.MSG, "Tipo extra inválido por similitud con fijo: " + tipoExtra));
                }
            }
            if (tipoExtra.chars().anyMatch(Character::isDigit)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(Constantes.MSG, "Tipo extra inválido: " + tipoExtra));
            }

            tipoNormalizado = tipoExtra;
        }

        // ---------------- VALIDACIÓN DE EXTENSIÓN ----------------
        Set<String> extensionesPermitidas = Constantes.EXT_PER;
        String extension = FilenameUtils.getExtension(archivo.getOriginalFilename()).toLowerCase();
        if (!extensionesPermitidas.contains(extension)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(Constantes.MSG, "Extensión no permitida: " + extension));
        }

        String nombreSeguro = UUID.randomUUID().toString() + "." + extension;
        Path destino = carpeta.resolve(nombreSeguro).normalize();
        if (!destino.startsWith(carpeta)) {
            throw new IOException("Ruta fuera del directorio permitido");
        }

        // ---------------- AUDITORÍA ----------------
        List<Metadata> existentes = metadataRepository.findByRegistroAndTipoDocumento(registro, tipoNormalizado);
        for (Metadata existente : existentes) {
            existente.setActivo(false);
            existente.setFechaDesactivacion(LocalDateTime.now());
            metadataRepository.save(existente);
        }

        Metadata metadata = new Metadata();
        metadata.setNombreArchivo(nombreSeguro);
        metadata.setTipoDocumento(tipoNormalizado);
        metadata.setFechaSubida(LocalDateTime.now());
        metadata.setRegistro(registro);
        metadata.setSubidoPor(usuario);
        metadata.setActivo(true);
        metadataRepository.save(metadata);

        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        String nombreLogico = tipoNormalizado + "_" + numeroSolicitud + "." + extension;
        ArchivoDTO dto = new ArchivoDTO(nombreLogico, Constantes.URL_DESC + numeroSolicitud + "/" + nombreSeguro, tipoNormalizado);

        return ResponseEntity.ok(Map.of(Constantes.MSG, "Archivo subido correctamente", Constantes.ARCHIVOS_CARP, dto));
    }



	
	//-----------------------CARGAR MULTIPLES IMAGENES AL MISMO TIEMPO------------------------------------
	
    @PostMapping("/subir-multiple/{numeroSolicitud}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Map<String, Object>> subirDocumentos(
            @PathVariable String numeroSolicitud,
            @RequestParam("archivos") List<MultipartFile> archivos,
            @RequestParam("tipos") List<String> tipos,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) throws IOException {

        Registro registro = registroRepository.findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constantes.NOT_FOUND));

        Usuario usuario = obtenerUsuario(userDetails, request, registro);

        final String solicitudNormalizada = numeroSolicitud.trim();

        Path carpeta = Paths.get(Constantes.ARCHIVOS_CARP, solicitudNormalizada);
        Files.createDirectories(carpeta);

        List<ArchivoDTO> archivosSubidos = procesarArchivos(
                archivos, tipos, userDetails, registro, usuario, carpeta, solicitudNormalizada);

        List<TipoDocumentoStatusDTO> status = Constantes.TIPOS_FIJOS.stream()
            .map(tipo -> {
                List<Metadata> metas = metadataRepository.findByRegistroAndTipoDocumentoAndActivoTrue(registro, tipo);
                if (!metas.isEmpty()) {
                    return new TipoDocumentoStatusDTO(tipo, true);
                } else {
                    return new TipoDocumentoStatusDTO(tipo, false);
                }
            })
            .toList();

        return ResponseEntity.ok(Map.of(
                Constantes.MSG, "Proceso de subida completado",
                Constantes.ARCHIVOS_CARP, archivosSubidos,
                "statusTipos", status
        ));
    }


    
    private Usuario obtenerUsuario(UserDetails userDetails, HttpServletRequest request, Registro registro) {
        if (userDetails != null) {
            Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO));
            boolean accesoPermitido = usuario.getRol().equals(Constantes.ADMIN)
                    || registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername());
            if (!accesoPermitido) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
            }
            return usuario;
        }
        String consumidor = (String) request.getAttribute("consumidor");
        if (consumidor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "API key inválida");
        }
        Usuario usuario = registro.getCreador();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El registro no tiene creador definido");
        }
        return usuario;
    }

    private List<ArchivoDTO> procesarArchivos(List<MultipartFile> archivos, List<String> tipos,
            UserDetails userDetails, Registro registro,
            Usuario usuario, Path carpeta, String numeroSolicitud) throws IOException {
		List<ArchivoDTO> archivosSubidos = new ArrayList<>();
		Set<String> tiposFijos = Constantes.TIPOS_FIJOS;
		Set<String> extensionesPermitidas = Constantes.EXT_PER;
		
		for (int i = 0; i < archivos.size(); i++) {
			MultipartFile archivo = archivos.get(i);
			String tipo = tipos.get(i);
			
			if (archivo == null || archivo.isEmpty() || tipo == null || tipo.isBlank()) {
			continue;
			}
			
			String tipoNormalizado = tipo.trim().toUpperCase();
			validarTipo(userDetails, tipoNormalizado, tiposFijos);
			
			String extension = FilenameUtils.getExtension(archivo.getOriginalFilename()).toLowerCase();
			validarExtension(extension, extensionesPermitidas);
			
			String nombreSeguro = UUID.randomUUID().toString() + "." + extension;
			Path destino = carpeta.resolve(nombreSeguro).normalize();
			validarRuta(destino, carpeta);
			
			desactivarMetadatosPrevios(registro, tipoNormalizado);
			
			Metadata metadata = crearMetadata(nombreSeguro, tipoNormalizado, registro, usuario);
			metadataRepository.save(metadata);
			
			Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
			
			String nombreLogico = tipoNormalizado + "_" + numeroSolicitud + "." + extension;
			archivosSubidos.add(new ArchivoDTO(nombreLogico, Constantes.URL_DESC + numeroSolicitud + "/" + nombreSeguro, tipoNormalizado));
		}
		return archivosSubidos;
    }
    private void validarTipo(UserDetails userDetails, String tipoNormalizado, Set<String> tiposFijos) {
        if (userDetails == null) {
            if (!tiposFijos.contains(tipoNormalizado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Con API Key solo se permiten los tipos fijos: " + tiposFijos);
            }
        } else {
            if (tipoNormalizado.chars().anyMatch(Character::isDigit)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo inválido: " + tipoNormalizado);
            }
        }
    }

    private void validarExtension(String extension, Set<String> extensionesPermitidas) {
        if (!extensionesPermitidas.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Extensión no permitida: " + extension);
        }
    }

    private void validarRuta(Path destino, Path carpeta) throws IOException {
        if (!destino.startsWith(carpeta)) {
            throw new IOException("Ruta fuera del directorio permitido");
        }
    }

    private void desactivarMetadatosPrevios(Registro registro, String tipoNormalizado) {
        List<Metadata> existentes = metadataRepository.findByRegistroAndTipoDocumento(registro, tipoNormalizado);
        for (Metadata existente : existentes) {
            existente.setActivo(false);
            existente.setFechaDesactivacion(LocalDateTime.now());
            metadataRepository.save(existente);
        }
    }

    private Metadata crearMetadata(String nombreSeguro, String tipoNormalizado, Registro registro, Usuario usuario) {
        Metadata metadata = new Metadata();
        metadata.setNombreArchivo(nombreSeguro);
        metadata.setTipoDocumento(tipoNormalizado);
        metadata.setFechaSubida(LocalDateTime.now());
        metadata.setRegistro(registro);
        metadata.setSubidoPor(usuario);
        metadata.setActivo(true);
        return metadata;
    }

	
	//-----------------------LISTAR REGISTROS-------------------------------------------------------------
	
    @GetMapping("/registros") 
    @PreAuthorize("hasAnyRole('USER','SUPERVISOR','ADMIN','SUPERADMIN')") 
    public ResponseEntity<List<RegistroDTO>> obtenerRegistros(@AuthenticationPrincipal UserDetails userDetails) {

	    Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO));

	    String rol = usuario.getRol();
	    String correoUsuario = usuario.getEmail();

	    List<Registro> registros;
	    
	    if (rol.equals(Constantes.SUPERADMIN)) { 
	    	registros = registroRepository.findByFechaEliminacionIsNull();
	    } else if (rol.equals(Constantes.ADMIN)) {
	        registros = registroRepository.findByFechaEliminacionIsNull();
	    } else if (rol.equals(Constantes.USER)) {
	        registros = registroRepository.findByFechaEliminacionIsNull().stream()
	            .filter(registro ->
	                (registro.getCreador() != null && registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername())) ||
	                registro.getCorreosAutorizados().stream()
	                    .map(CorreoAutorizado::getCorreo)
	                    .anyMatch(correo -> correo.equalsIgnoreCase(correoUsuario)))
	            .toList();
	    } else if (rol.equals(Constantes.SUPERVISOR)) {
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
	                registro.getCreador() != null ? registro.getCreador().getUsername() : Constantes.USER_DESC,
	                registro.getFechaCreacion(),
	                correos
	            );
	        })
	        .toList();

	    return ResponseEntity.ok(respuesta);
	}
	
//--------------------------OBTENER REGISTRO POR NUMERO DE SOLICITUD-------------------------------------------------------------------------------------
	
    @GetMapping("/registros/{numeroSolicitud}") 
    @PreAuthorize("hasAnyRole('USER','SUPERVISOR','ADMIN','SUPERADMIN')") 
    public ResponseEntity<RegistroDTO> obtenerRegistro( @PathVariable String numeroSolicitud, 
    													@AuthenticationPrincipal UserDetails userDetails) {

	    //--------Validar usuario autenticado--------
	    Usuario usuario = usuarioRepository.findByUsername(userDetails.getUsername())
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO));

	    //--------Buscar el registro
	    Registro registro = registroRepository.findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constantes.NOT_FOUND));

	    //--------Correos autorizados (mantiene lógica actual)
	    List<String> correos = correoAutorizadoRepository.findByRegistroId(registro.getId())
	        .stream()
	        .map(CorreoAutorizado::getCorreo)
	        .filter(c -> registro.getCreador() != null && !c.equalsIgnoreCase(registro.getCreador().getEmail()))
	        .toList();

	    //--------Carpeta física
	    Path carpeta = Paths.get(Constantes.ARCHIVOS_CARP, numeroSolicitud.trim()).normalize();

	    //--------Obtener solo metadatos activos desde BD
	    List<Metadata> activos = metadataRepository.findByRegistroAndActivoTrueAndFechaDesactivacionIsNull(registro);

	    //--------Filtrar: solo los que existen físicamente en la carpeta
	    List<ArchivoDTO> archivos = Files.exists(carpeta)
	        ? activos.stream()
	            .filter(meta -> {
	                Path rutaArchivo = carpeta.resolve(meta.getNombreArchivo()).normalize();
	                return Files.exists(rutaArchivo) && Files.isRegularFile(rutaArchivo);
	            })
	            .map(meta -> {
	                String extension = FilenameUtils.getExtension(meta.getNombreArchivo());
	                String nombreVisual = meta.getTipoDocumento() + "_" + numeroSolicitud + "." + extension;
	                String urlDescarga = Constantes.URL_DESC + numeroSolicitud + "/" + meta.getNombreArchivo();
	                return new ArchivoDTO(nombreVisual, urlDescarga, meta.getTipoDocumento());
	            })
	            .toList()
	        : Collections.emptyList();

	    //--------Validar si es dueño (mantiene lógica actual)
	    boolean esDueno = usuario.getRol().equals(Constantes.ADMIN) ||
	                      (registro.getCreador() != null &&
	                       registro.getCreador().getUsername().equalsIgnoreCase(usuario.getUsername()));

	    //--------Construir DTO
	    RegistroDTO dto = new RegistroDTO(
	        registro.getNumeroSolicitud(),
	        registro.getCarpetaRuta(),
	        registro.getCreador() != null ? registro.getCreador().getUsername() : Constantes.USER_DESC,
	        registro.getFechaCreacion(),
	        correos
	    );
	    dto.setImagenes(archivos);
	    dto.setEsDueño(esDueno);

	    return ResponseEntity.ok(dto);
	}
	
	//----------------------LISTAR USUARIOS Y MOSTRAR CONTRASEÑA -----------------------------------------------
	
    @GetMapping("/usuarios") 
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')") 
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios(@AuthenticationPrincipal UserDetails userDetails) { 
    	Usuario solicitante = usuarioRepository.findByUsername(userDetails.getUsername()) 
    			.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, Constantes.NO_AUTORIZADO)); 
    	
    	// ✅ Permitimos tanto ADMIN como SUPERADMIN 
    	if (!(Constantes.ADMIN.equals(solicitante.getRol()) || Constantes.SUPERADMIN.equals(solicitante.getRol()))) { 
    		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado"); 
    		} 
    	List<Usuario> usuarios = usuarioRepository.findAll(); 
    	
    	List<UsuarioDTO> respuesta = usuarios.stream()
    			.map(usuario -> { 
    				String desencriptada = null; 
    				PasswordEncriptada pass = usuario.getPasswordEncriptada(); 
    				if (pass != null && pass.getHash() != null) { 
    					try { 
    						desencriptada = AESUtil.decrypt(pass.getHash()); 
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
    						desencriptada // ✅ ahora sí se devuelve la contraseña desencriptada 
    					); 
    				}) 
    			.toList(); 
    	return ResponseEntity.ok(respuesta); 
    }
	
//	----------------------DESCARGAR IMAGENES------------------------------------------

    @GetMapping("/descargar/{numeroSolicitud}/{nombreArchivo}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Resource> descargarArchivo(
            @PathVariable String numeroSolicitud,
            @PathVariable String nombreArchivo,
            @RequestParam(defaultValue = "false") boolean inline) throws IOException {

        Path ruta = Paths.get(Constantes.ARCHIVOS_CARP, numeroSolicitud).resolve(nombreArchivo).normalize();
        Resource recurso = new UrlResource(ruta.toUri());

        if (!recurso.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(ruta);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        Metadata metadata = metadataRepository.findByNombreArchivo(nombreArchivo)
                .orElse(null);

        String tipoDocumento = metadata != null ? metadata.getTipoDocumento() : "archivo";
        String extension = FilenameUtils.getExtension(nombreArchivo);
        String nombreDescarga = tipoDocumento + "." + extension;

        String disposition = inline
                ? "inline; filename=\"" + nombreDescarga + "\""
                : "attachment; filename=\"" + nombreDescarga + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(recurso);
    }

}
