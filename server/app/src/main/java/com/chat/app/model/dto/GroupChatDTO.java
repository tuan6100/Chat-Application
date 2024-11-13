package com.chat.app.model.dto;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
public class GroupChatDTO extends ChatRoomDTO{

    private HashSet<Account> members;
    private HashSet<Account> admins;
    private boolean permission;

    public GroupChatDTO(String roomName, String avatar, Theme theme, HashSet<Account> members, HashSet<Account> admins, boolean permission) {
        super(roomName, avatar, theme);
        this.members = members;
        this.admins = admins;
        this.permission = permission;
    }

}
