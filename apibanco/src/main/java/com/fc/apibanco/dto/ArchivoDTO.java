package com.fc.apibanco.dto;

public class ArchivoDTO {
    private final String nombre;
    private final String url;

    public ArchivoDTO(String nombre, String url) {
        this.nombre = nombre;
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ArchivoDTO{" +
                "nombre='" + nombre + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
