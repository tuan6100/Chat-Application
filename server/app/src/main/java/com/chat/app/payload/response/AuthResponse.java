package com.chat.app.payload.response;


import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private Boolean isAuthenticated;

    public AuthResponse(String jwt, Boolean isAuthenticated) {
        this.jwt = jwt;
        this.isAuthenticated = isAuthenticated;
    }
}
