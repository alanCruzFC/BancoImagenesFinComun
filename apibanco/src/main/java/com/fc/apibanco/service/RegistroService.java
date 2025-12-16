package com.fc.apibanco.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fc.apibanco.dto.RegistroRequest;
import com.fc.apibanco.model.CorreoAutorizado;
import com.fc.apibanco.model.Registro;
import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.RegistroRepository;
import com.fc.apibanco.repository.UsuarioRepository;

@Service
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final RegistroRepository registroRepository;
    
    public RegistroService(UsuarioRepository usuarioRepository, RegistroRepository registroRepository) {
    	this.registroRepository = registroRepository;
    	this.usuarioRepository = usuarioRepository;
    }

    private static final Path BASE_PATH = Paths.get("Archivos").normalize();

    public Registro crearRegistro(RegistroRequest request, String creadorUsername) {
        String numeroSolicitud = request.getNumeroSolicitud().trim();

        Optional<Registro> existente = registroRepository
            .findByNumeroSolicitudAndFechaEliminacionIsNull(numeroSolicitud);
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe un registro con el número de solicitud " + numeroSolicitud);
        }

        Usuario creador = usuarioRepository.findByEmail(creadorUsername)
            .orElseGet(() -> usuarioRepository.findByUsername(creadorUsername).orElse(null));

        if (creador == null) {
            creador = new Usuario();
            creador.setUsername(creadorUsername);
            creador.setEmail(creadorUsername);
            creador.setRol("USER");
            creador.setActivo(true);
            creador.setPasswordHash(null);
            creador.setPasswordEncriptada(null);
            creador = usuarioRepository.save(creador);
        }

        Path carpeta = BASE_PATH.resolve(numeroSolicitud).normalize();
        if (!carpeta.startsWith(BASE_PATH)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Ruta fuera del directorio permitido");
        }

        try {
            Files.createDirectories(carpeta);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta para la solicitud " + numeroSolicitud, e);
        }

        Registro registro = new Registro();
        registro.setNumeroSolicitud(numeroSolicitud);
        registro.setFechaCreacion(LocalDateTime.now());
        registro.setCreador(creador);
        registro.setCarpetaRuta(carpeta.toString());

        List<CorreoAutorizado> autorizados = request.getCorreosAutorizados().stream()
            .map(correo -> {
                CorreoAutorizado ca = new CorreoAutorizado();
                ca.setCorreo(correo);
                ca.setRegistro(registro);
                return ca;
            })
            .toList();
        registro.setCorreosAutorizados(autorizados);

        for (String correo : request.getCorreosAutorizados()) {
            Usuario usuario = usuarioRepository.findByEmail(correo).orElseGet(() -> {
                Usuario nuevo = new Usuario();
                nuevo.setUsername(correo);
                nuevo.setEmail(correo);
                nuevo.setRol("USER");
                nuevo.setActivo(true);
                return usuarioRepository.save(nuevo);
            });
        }

        return registroRepository.save(registro);
    }
}
