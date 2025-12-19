package com.fc.apibanco.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fc.apibanco.model.ApiKey;
import com.fc.apibanco.repository.ApiKeyRepository;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);

    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public String validateAndGetConsumer(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Validación fallida: API key nula o vacía");
            return null;
        }
        return apiKeyRepository.findByClaveAndFechaEliminacionIsNull(apiKey)
                .filter(ApiKey::isActivo)
                .map(ApiKey::getConsumidor)
                .orElse(null);
    }

    public Optional<ApiKey> findByClave(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Búsqueda fallida: API key nula o vacía");
            return Optional.empty();
        }
        return apiKeyRepository.findByClaveAndFechaEliminacionIsNull(apiKey);
    }
    
    public void desactivarApiKey(Long id) { 
    	ApiKey apiKey = apiKeyRepository.findById(id) 
    			.orElseThrow(() -> new RuntimeException("API Key no encontrada")); 
    	apiKey.setActivo(false); apiKey.setFechaEliminacion(LocalDateTime.now()); 
    	apiKeyRepository.save(apiKey); 
    } 
    
    public List<ApiKey> listarActivas() { 
    	return apiKeyRepository.findByActivoTrue(); 
    }
}
