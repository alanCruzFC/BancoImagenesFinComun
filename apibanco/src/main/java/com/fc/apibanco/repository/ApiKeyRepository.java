package com.fc.apibanco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fc.apibanco.model.ApiKey;


public interface ApiKeyRepository extends JpaRepository<ApiKey, Long>{
	Optional<ApiKey> findByClaveAndFechaEliminacionIsNull(String clave);

}
