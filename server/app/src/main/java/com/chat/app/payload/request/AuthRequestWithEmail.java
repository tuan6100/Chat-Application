package com.chat.app.payload.request;

public class AuthRequestWithEmail {
    private String email;
    private String password;

    public AuthRequestWithEmail(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
