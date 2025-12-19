package com.fc.apibanco.dto;

import java.util.List;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@Data
public class RegistroRequest {
    
    @NotBlank(message = "El número de solicitud es obligatorio")
    private String numeroSolicitud;

    @NotEmpty(message = "Debe proporcionar al menos un correo autorizado")
    private List<String> correosAutorizados;

	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public void setNumeroSolicitud(String numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
	}

	public List<String> getCorreosAutorizados() {
		return correosAutorizados;
	}

	public void setCorreosAutorizados(List<String> correosAutorizados) {
		this.correosAutorizados = correosAutorizados;
	}
    
    
}
