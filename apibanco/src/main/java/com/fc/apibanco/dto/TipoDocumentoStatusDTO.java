package com.fc.apibanco.dto;

public class TipoDocumentoStatusDTO {
    private String tipoDocumento;
    private boolean cargado;

    public TipoDocumentoStatusDTO(String tipoDocumento, boolean cargado) {
        this.tipoDocumento = tipoDocumento;
        this.cargado = cargado;
    }

    public String getTipoDocumento() { return tipoDocumento; }
    public boolean isCargado() { return cargado; }
}
