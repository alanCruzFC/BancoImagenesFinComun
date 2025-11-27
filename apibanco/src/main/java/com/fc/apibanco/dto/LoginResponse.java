package com.fc.apibanco.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class LoginResponse {
    private String token;
    public LoginResponse(String token) {
        this.token = token;
    }
    
	public String getToken() {
		return token;
	}
    
}


