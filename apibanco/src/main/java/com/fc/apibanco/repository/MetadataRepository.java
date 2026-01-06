package com.fc.apibanco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fc.apibanco.model.Metadata;
import com.fc.apibanco.model.Registro;

public interface MetadataRepository extends JpaRepository<Metadata, Long> {
	
    List<Metadata> findByRegistro_NumeroSolicitud(String numeroSolicitud);
    
	List<Metadata> findByRegistroAndTipoDocumento(Registro registro, String tipo);
	
	List<Metadata> findByRegistroAndActivoTrue(Registro registro);
	
	List<Metadata> findByRegistroAndActivoTrueAndFechaDesactivacionIsNull(Registro registro);
	
	Optional<Metadata> findByNombreArchivo(String nombreArchivo);
	
	List<Metadata> findByRegistroAndTipoDocumentoAndActivoTrue(Registro registro, String tipoDocumento);
}

