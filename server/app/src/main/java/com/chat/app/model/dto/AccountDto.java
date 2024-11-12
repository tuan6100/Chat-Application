package com.chat.app.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountDto {
    private String username;
//    private String password;
//    private String email;
    private String avatarImagePath;
    private Date birthDate;
    private Character gender;
    private String bio;

}
