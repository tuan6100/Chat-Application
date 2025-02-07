package com.chat.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    private String email;
    private String oldPassword;
    private String newPassword;

}
