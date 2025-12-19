package com.fc.apibanco.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fc.apibanco.dto.ApiKeyRequest;
import com.fc.apibanco.model.ApiKey;
import com.fc.apibanco.repository.ApiKeyRepository;
import com.fc.apibanco.service.ApiKeyService;
import com.fc.apibanco.util.Constantes;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    
    private final ApiKeyRepository apiKeyRepository;
    
    public ApiKeyController(ApiKeyRepository apiKeyRepository, ApiKeyService apiKeyService) { 
        this.apiKeyRepository = apiKeyRepository; 
        this.apiKeyService = apiKeyService; 
    }
    
    //---------------------LISTAR APIKEYS------------------------------------------------------------------------------
    @GetMapping("/apikeys")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<List<ApiKey>> listarApiKeys() {
        List<ApiKey> claves = apiKeyRepository.findAll();
        return ResponseEntity.ok(claves);
    }

    //------------------------CREAR UNA API KEY-------------------------------------------------------------------------    
    @PostMapping("/apikeys")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> crearApiKey(@RequestBody ApiKeyRequest request){
        
        String claveGenerada = UUID.randomUUID().toString();
        
        ApiKey apiKey = new ApiKey();
        apiKey.setClave(claveGenerada);
        apiKey.setConsumidor(request.getConsumidor());
        apiKey.setLectura(request.isLectura());
        apiKey.setEscritura(request.isEscritura());
        apiKey.setActualizacion(request.isActualizacion());
        apiKey.setEliminacion(request.isEliminacion());
        apiKey.setFechaCreacion(LocalDateTime.now());
        apiKey.setFechaEliminacion(null);
        
        apiKeyRepository.save(apiKey);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            Constantes.MSG, "API key Creada Exitosamente"
        ));
    }

    //-------------------------REGENERAR API KEY--------------------------------------------------------------------------
    @PutMapping("/apikeys/{id}/refactorizar")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> regenerarApiKey(@PathVariable Long id) {

        ApiKey apiKey = apiKeyRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Constantes.NOT_FOUND));

        String nuevaClave = UUID.randomUUID().toString().replace("-", "");
        apiKey.setClave(nuevaClave);
        apiKey.setFechaEliminacion(null);

        apiKeyRepository.save(apiKey);

        return ResponseEntity.ok(Map.of(
            Constantes.MSG, "API key regenerada exitosamente",
            "nuevaClave", nuevaClave
        ));
    }
    
    //-------------------------------ACTUALIZAR API KEY------------------------------------------------------------------
    @PutMapping("/apikeys/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> actualizarApiKey(@PathVariable Long id, @RequestBody ApiKey apiKeyActualizada) {
        return apiKeyRepository.findById(id)
            .map(apiKey -> {

                apiKey.setConsumidor(apiKeyActualizada.getConsumidor());
                apiKey.setActivo(apiKeyActualizada.isActivo());
                apiKey.setLectura(apiKeyActualizada.isLectura());
                apiKey.setEscritura(apiKeyActualizada.isEscritura());
                apiKey.setActualizacion(apiKeyActualizada.isActualizacion());
                apiKey.setEliminacion(apiKeyActualizada.isEliminacion());

                if (apiKeyActualizada.getClave() != null && !apiKeyActualizada.getClave().isBlank()) {
                    apiKey.setClave(apiKeyActualizada.getClave());
                }

                apiKeyRepository.save(apiKey);
                return ResponseEntity.ok("✅ API Key actualizada correctamente");
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ API Key no encontrada"));
    }
    
    @DeleteMapping("/apikeys/{id}") 
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> borrarApiKey(@PathVariable Long id) { 
    	apiKeyService.desactivarApiKey(id); 
    	return ResponseEntity.noContent().build(); 
    }
    
    @GetMapping 
    public ResponseEntity<List<ApiKey>> listarActivas() { 
    	return ResponseEntity.ok(apiKeyService.listarActivas()); 
    }
}
