package com.chat.app.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountDTO {
    private String avatar;
    private Date birthdate;
    private Character gender;
    private String bio;

}
