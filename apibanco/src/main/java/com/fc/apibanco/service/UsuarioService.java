package com.fc.apibanco.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.UsuarioRepository;

@Service
public class UsuarioService {

    
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
    	this.usuarioRepository = usuarioRepository;
    }

    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuario.setDeletedAt(LocalDateTime.now());

        usuarioRepository.save(usuario);
    }
}

