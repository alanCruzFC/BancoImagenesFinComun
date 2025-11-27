package com.fc.apibanco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fc.apibanco.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String correo);

}
