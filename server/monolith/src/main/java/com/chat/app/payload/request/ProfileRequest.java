package com.chat.app.payload.request;

import lombok.Getter;

import java.util.Date;

@Getter
public class ProfileRequest {

    private String avatar;
    private Date birthDate;
    private Character gender;
    private String bio;

}
