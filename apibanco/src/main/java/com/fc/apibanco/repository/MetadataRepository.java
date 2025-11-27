package com.fc.apibanco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fc.apibanco.model.Metadata;

public interface MetadataRepository extends JpaRepository<Metadata, Long> {
    List<Metadata> findByRegistro_NumeroSolicitud(String numeroSolicitud);
}

