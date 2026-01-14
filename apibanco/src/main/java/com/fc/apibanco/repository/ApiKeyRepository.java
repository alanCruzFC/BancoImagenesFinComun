package com.fc.apibanco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fc.apibanco.model.ApiKey;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long>{
	Optional<ApiKey> findByClaveAndFechaEliminacionIsNull(String clave);

	List<ApiKey> findByActivoTrue();
}
