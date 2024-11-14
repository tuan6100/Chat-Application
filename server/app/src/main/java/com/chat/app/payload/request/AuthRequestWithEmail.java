package com.chat.app.payload.request;

import lombok.Data;

@Data
public class AuthRequestWithEmail {
    private String email;
    private String password;

    public AuthRequestWithEmail(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
