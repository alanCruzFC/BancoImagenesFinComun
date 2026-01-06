package com.fc.apibanco.dto;

public class TipoDocumentoStatusDTO {
    private String tipoDocumento;
    private boolean cargado;
    private String url;

    public TipoDocumentoStatusDTO(String tipoDocumento, boolean cargado, String url) {
        this.tipoDocumento = tipoDocumento;
        this.cargado = cargado;
        this.url = url;
    }

    public String getTipoDocumento() { return tipoDocumento; }
    public boolean isCargado() { return cargado; }
    public String getUrl() { return url; }
}
