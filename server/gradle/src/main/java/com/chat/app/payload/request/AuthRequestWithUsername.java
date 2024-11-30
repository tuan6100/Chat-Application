package com.chat.app.payload.request;

import lombok.Data;

@Data
public class AuthRequestWithUsername {
    private String username;
    private String password;

    public AuthRequestWithUsername(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
