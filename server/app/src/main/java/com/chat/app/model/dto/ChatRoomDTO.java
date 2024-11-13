package com.chat.app.model.dto;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chatroom.GroupChat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDTO {

    protected String roomName;
    protected String avatar;
    protected Theme theme;

    public ChatRoomDTO(String roomName, String avatar, Theme theme) {
        this.roomName = roomName;
        this.avatar = avatar;
        this.theme = theme;
    }

}
