package com.chat.app.payload.request;

import lombok.Data;

import java.util.Date;

@Data
public class ProfileRequest {

    private String avatar;
    private Date birthDate;
    private Character gender;
    private String bio;

}
