package com.chat.app.payload.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class AccountRequest {

    private Long accountId;
    private String username;
    private String avatar;
    private Date birthdate;
    private Character gender;
    private String bio;
    private String phoneNumber;

}
