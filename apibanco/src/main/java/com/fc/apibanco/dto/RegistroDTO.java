package com.fc.apibanco.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RegistroDTO {
    private String numeroSolicitud;
    private String carpetaRuta;
    private String creador;
    private LocalDateTime fechaCreacion;
    private List<String> correosAutorizados;
    private List<ArchivoDTO> imagenes;
    private boolean esDueño;
    

    public RegistroDTO(String numeroSolicitud, String carpetaRuta, String creador,
		            LocalDateTime fechaCreacion, List<String> correosAutorizados) {
		this.numeroSolicitud = numeroSolicitud;
		this.carpetaRuta = carpetaRuta;
		this.creador = creador;
		this.fechaCreacion = fechaCreacion;
		this.correosAutorizados = correosAutorizados;
		}

	public RegistroDTO(String numeroSolicitud, String carpetaRuta, String creador, LocalDateTime fechaCreacion,
			List<String> correosAutorizados, List<ArchivoDTO> imagenes, boolean esDueño) {
		super();
		this.numeroSolicitud = numeroSolicitud;
		this.carpetaRuta = carpetaRuta;
		this.creador = creador;
		this.fechaCreacion = fechaCreacion;
		this.correosAutorizados = correosAutorizados;
		this.imagenes = imagenes;
		this.esDueño = esDueño;
	}

	public String getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public String getCarpetaRuta() {
		return carpetaRuta;
	}

	public String getCreador() {
		return creador;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public List<String> getCorreosAutorizados() {
		return correosAutorizados;
	}

	public List<ArchivoDTO> getImagenes() {
		return imagenes;
	}

	public boolean isEsDueño() {
		return esDueño;
	}

	public void setImagenes(List<ArchivoDTO> imagenes) {
		this.imagenes = imagenes;
	}

	public void setEsDueño(boolean esDueño) {
		this.esDueño = esDueño;
	}
    
}

