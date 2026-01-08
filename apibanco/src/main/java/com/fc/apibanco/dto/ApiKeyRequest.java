package com.fc.apibanco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApiKeyRequest {
    
    @NotBlank(message = "El consumidor es obligatorio")
    private String consumidor;
    
    private boolean lectura;
    private boolean escritura; 
    private boolean actualizacion;
    private boolean eliminacion;
    
	public String getConsumidor() {
		return consumidor;
	}
	public boolean isLectura() {
		return lectura;
	}
	public boolean isEscritura() {
		return escritura;
	}
	public boolean isActualizacion() {
		return actualizacion;
	}
	public boolean isEliminacion() {
		return eliminacion;
	}
}
