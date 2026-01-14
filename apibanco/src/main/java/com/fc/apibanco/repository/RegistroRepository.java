package com.fc.apibanco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fc.apibanco.model.Registro;

@Repository
public interface RegistroRepository extends JpaRepository<Registro, Long> {
	
    Optional<Registro> findByNumeroSolicitudAndFechaEliminacionIsNull(String numeroSolicitud);
    
    List<Registro> findByFechaEliminacionIsNull();

}
