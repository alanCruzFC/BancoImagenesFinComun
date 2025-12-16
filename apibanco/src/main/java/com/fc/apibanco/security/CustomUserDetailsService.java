package com.fc.apibanco.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fc.apibanco.model.Usuario;
import com.fc.apibanco.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UsuarioRepository usuarioRepository;
    
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
    	this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new org.springframework.security.core.userdetails.User(
        	    usuario.getUsername(),
        	    usuario.getPasswordHash(),
        	    usuario.isActivo(),
        	    true, true, true,
        	    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()))
        	);

    }
}
