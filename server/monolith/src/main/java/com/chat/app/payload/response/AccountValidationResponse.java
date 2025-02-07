package com.chat.app.payload.response;

import lombok.Data;

@Data
public class AccountValidationResponse {

    private String username;
    private String avatar;

    public AccountValidationResponse(String username, String avatar) {
        this.username = username;
        this.avatar = avatar;
    }
}
