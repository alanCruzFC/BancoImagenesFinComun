package com.fc.apibanco.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fc.apibanco.dto.RegistroRequest;
import com.fc.apibanco.model.CorreoAutorizado;
import com.fc.apibanco.model.Registro;
import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.CorreoAutorizadoRepository;
import com.fc.apibanco.repository.RegistroRepository;
import com.fc.apibanco.repository.UsuarioRepository;

@Service
public class RegistroService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RegistroRepository registroRepository;

    public Registro crearRegistro(RegistroRequest request, String username) {
        Usuario creador = usuarioRepository.findByEmail(username)
            .orElseGet(() -> usuarioRepository.findByUsername(username).orElse(null));
        
        if (creador == null) {
            creador = new Usuario();
            creador.setUsername(username);
            creador.setEmail(username);
            creador.setRol("USER");
            creador.setActivo(true);
            creador.setPasswordHash(null);
            creador.setPasswordEncriptada(null);
            creador = usuarioRepository.save(creador);

            System.out.println("Usuario creador creado: " + username);
        }
        
        String carpetaRuta = "Archivos/" + request.getNumeroSolicitud() + username;
        File carpeta = new File(carpetaRuta);
        if (!carpeta.exists()) {
        	boolean creada = carpeta.mkdirs();
        	if(!creada) {
        		throw new RuntimeException("No se pudo crear la carpeta para la solicitud " + request.getNumeroSolicitud());
            }
            System.out.println("Carpeta creada en: " + carpeta.getAbsolutePath());
        }

        Registro registro = new Registro();
        registro.setNumeroSolicitud(request.getNumeroSolicitud());
        registro.setFechaCreacion(LocalDateTime.now());
        registro.setCreador(creador);
        registro.setCarpetaRuta("Archivos/" + request.getNumeroSolicitud());
        
        List<CorreoAutorizado> autorizados = request.getCorreosAutorizados().stream()
                .map(correo -> {
                    CorreoAutorizado ca = new CorreoAutorizado();
                    ca.setCorreo(correo);
                    ca.setRegistro(registro); // relación bidireccional
                    return ca;
                })
                .toList();

            // Asignar la lista al registro
            registro.setCorreosAutorizados(autorizados);

        for (String correo : request.getCorreosAutorizados()) {
            usuarioRepository.findByEmail(correo).orElseGet(() -> {
                Usuario nuevo = new Usuario();
                nuevo.setUsername(correo);
                nuevo.setEmail(correo);
                nuevo.setRol("USER");
                nuevo.setActivo(true);
                nuevo.setPasswordHash(null);
                nuevo.setPasswordEncriptada(null);
                usuarioRepository.save(nuevo);

                System.out.println("Usuario autorizado creado sin contraseña: " + correo);
                return nuevo;
            });
        }

        return registroRepository.save(registro);
    }
}
