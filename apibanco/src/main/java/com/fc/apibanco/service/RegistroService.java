package com.fc.apibanco.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RegistroRepository registroRepository;

    public Registro crearRegistro(RegistroRequest request, String creadorUsername) {
        Optional<Registro> existente = registroRepository
            .findByNumeroSolicitudAndFechaEliminacionIsNull(request.getNumeroSolicitud());
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Ya existe un registro con el número de solicitud " + request.getNumeroSolicitud());
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

        String carpetaRuta = "Archivos/" + request.getNumeroSolicitud();
        File carpeta = new File(carpetaRuta);
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new RuntimeException("No se pudo crear la carpeta para la solicitud " + request.getNumeroSolicitud());
        }

        Registro registro = new Registro();
        registro.setNumeroSolicitud(request.getNumeroSolicitud());
        registro.setFechaCreacion(LocalDateTime.now());
        registro.setCreador(creador);
        registro.setCarpetaRuta(carpetaRuta);

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
            usuarioRepository.findByEmail(correo).orElseGet(() -> {
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