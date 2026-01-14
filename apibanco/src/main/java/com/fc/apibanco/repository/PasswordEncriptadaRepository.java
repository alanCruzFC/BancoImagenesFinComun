package com.fc.apibanco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fc.apibanco.model.PasswordEncriptada;

@Repository
public interface PasswordEncriptadaRepository extends JpaRepository<PasswordEncriptada, Long> {
    Optional<PasswordEncriptada> findByUsuario_Username(String username);
}
