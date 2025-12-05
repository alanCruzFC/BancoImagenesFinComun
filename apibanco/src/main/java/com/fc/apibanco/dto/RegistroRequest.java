package com.fc.apibanco.dto;

import java.util.List;

import lombok.Data;

@Data
public class RegistroRequest {
    private String numeroSolicitud;
    private List<String> correosAutorizados;
    
	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}
	public List<String> getCorreosAutorizados() {
		return correosAutorizados;
	} 
}

