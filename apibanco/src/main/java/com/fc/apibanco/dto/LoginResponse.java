package com.fc.apibanco.dto;


public class LoginResponse {
    private final String token;
    private final String rol;

    public LoginResponse(String token, String rol) {
        this.token = token;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "token='" + token + '\'' +
                ", rol='" + rol + '\'' +
                '}';
    }
}
