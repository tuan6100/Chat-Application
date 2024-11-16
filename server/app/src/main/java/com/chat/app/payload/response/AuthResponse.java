package com.chat.app.payload.response;


import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Boolean isAuthenticated;

    public AuthResponse(String accessToken, String refreshToken, Boolean isAuthenticated) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isAuthenticated = isAuthenticated;
    }
}
