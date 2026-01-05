package com.fc.apibanco.dto;

public class ArchivoDTO {
    private final String nombre;
    private final String url;
    private final String tipoDocumento;

    public ArchivoDTO(String nombre, String url, String tipoDocumento) {
        this.nombre = nombre;
        this.url = url;
        this.tipoDocumento = tipoDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    public String getTipoDocumento() {
		return tipoDocumento;
	}

	@Override
    public String toString() {
        return "ArchivoDTO{" +
                "nombre='" + nombre + '\'' +
                ", url='" + url + '\'' +
                ", tipoDocumento='" + tipoDocumento + '\'' +
                '}';
    }
}
