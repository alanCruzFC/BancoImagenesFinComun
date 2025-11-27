package com.fc.apibanco.dto;

public class ArchivoDTO {
    private String nombre;
    private String url;
    
    
	public ArchivoDTO(String nombre, String url) {
		super();
		this.nombre = nombre;
		this.url = url;
	}


	public String getNombre() {
		return nombre;
	}


	public String getUrl() {
		return url;
	}
	
	
    
    
}

