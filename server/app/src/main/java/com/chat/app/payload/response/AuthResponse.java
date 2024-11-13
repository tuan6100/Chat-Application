package com.chat.app.payload.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthResponse {
    private String jwt;
    private Boolean isAuthenticated;
}
