package com.fc.apibanco.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RegistroDTO {
    private final String numeroSolicitud;
    private final String carpetaRuta;
    private final String creador;
    private final LocalDateTime fechaCreacion;
    private final List<String> correosAutorizados;
    private List<ArchivoDTO> imagenes;
    private boolean esDueno;

    public RegistroDTO(String numeroSolicitud, String carpetaRuta, String creador,
                       LocalDateTime fechaCreacion, List<String> correosAutorizados) {
        this.numeroSolicitud = numeroSolicitud;
        this.carpetaRuta = carpetaRuta;
        this.creador = creador;
        this.fechaCreacion = fechaCreacion;
        this.correosAutorizados = correosAutorizados;
    }

    public RegistroDTO(String numeroSolicitud, String carpetaRuta, String creador,
                       LocalDateTime fechaCreacion, List<String> correosAutorizados,
                       List<ArchivoDTO> imagenes, boolean esDueño) {
        this.numeroSolicitud = numeroSolicitud;
        this.carpetaRuta = carpetaRuta;
        this.creador = creador;
        this.fechaCreacion = fechaCreacion;
        this.correosAutorizados = correosAutorizados;
        this.imagenes = imagenes;
        this.esDueno = esDueño;
    }

    public String getNumeroSolicitud() { return numeroSolicitud; }
    public String getCarpetaRuta() { return carpetaRuta; }
    public String getCreador() { return creador; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public List<String> getCorreosAutorizados() { return correosAutorizados; }
    public List<ArchivoDTO> getImagenes() { return imagenes; }
    public boolean isEsDueño() { return esDueno; }

    public void setImagenes(List<ArchivoDTO> imagenes) { this.imagenes = imagenes; }
    public void setEsDueño(boolean esDueño) { this.esDueno = esDueño; }

    @Override
    public String toString() {
        return "RegistroDTO{" +
                "numeroSolicitud='" + numeroSolicitud + '\'' +
                ", carpetaRuta='" + carpetaRuta + '\'' +
                ", creador='" + creador + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", correosAutorizados=" + correosAutorizados +
                ", imagenes=" + imagenes +
                ", esDueño=" + esDueno +
                '}';
    }
}
