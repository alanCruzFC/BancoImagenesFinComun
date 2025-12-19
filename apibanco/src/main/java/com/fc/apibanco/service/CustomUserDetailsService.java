package com.fc.apibanco.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Construir authorities: por ahora un solo rol, pero preparado para múltiples
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));

        return new org.springframework.security.core.userdetails.User(
            usuario.getUsername(),
            usuario.getPasswordHash(),
            usuario.isActivo(),   // habilitado según flag activo
            true,                 // accountNonExpired
            true,                 // credentialsNonExpired
            true,                 // accountNonLocked
            authorities
        );
    }
}
