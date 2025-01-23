package com.chat.app.dto;

import com.chat.app.enumeration.Theme;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDTO {

    protected String roomName;
    protected String avatar;
    protected Theme theme;

    public ChatDTO(String roomName, String avatar, Theme theme) {
        this.roomName = roomName;
        this.avatar = avatar;
        this.theme = theme;
    }

}
