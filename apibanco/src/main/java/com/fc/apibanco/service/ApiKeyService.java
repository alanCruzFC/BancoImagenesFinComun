package com.fc.apibanco.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fc.apibanco.model.ApiKey;
import com.fc.apibanco.repository.ApiKeyRepository;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    
    public ApiKeyService(ApiKeyRepository apiKeyRepository) {
    	this.apiKeyRepository = apiKeyRepository;
    }

    public String validateAndGetConsumer(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) return null;
        return apiKeyRepository.findByClaveAndFechaEliminacionIsNull(apiKey)
                .filter(ApiKey::isActivo)
                .map(ApiKey::getConsumidor)
                .orElse(null);
    }

    public Optional<ApiKey> findByClave(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) return Optional.empty();
        return apiKeyRepository.findByClaveAndFechaEliminacionIsNull(apiKey);
    }
}
