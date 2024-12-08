package com.chat.app.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AccountDTO {
    private String avatar;
    private Date birthdate;
    private Character gender;
    private String bio;

}
