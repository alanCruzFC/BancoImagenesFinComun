package com.fc.apibanco.dto;

import jakarta.validation.constraints.NotBlank;

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
    public void setConsumidor(String consumidor) {
        this.consumidor = consumidor;
    }
    public boolean isLectura() {
        return lectura;
    }
    public void setLectura(boolean lectura) {
        this.lectura = lectura;
    }
    public boolean isEscritura() {
        return escritura;
    }
    public void setEscritura(boolean escritura) {
        this.escritura = escritura;
    }
    public boolean isActualizacion() {
        return actualizacion;
    }
    public void setActualizacion(boolean actualizacion) {
        this.actualizacion = actualizacion;
    }
    public boolean isEliminacion() {
        return eliminacion;
    }
    public void setEliminacion(boolean eliminacion) {
        this.eliminacion = eliminacion;
    }
}
