package com.chat.app.model.dto;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class GroupChatDTO extends ChatDTO {

    private Set<Account> members;
    private Set<Account> admins;
    private boolean permission;

    public GroupChatDTO(String roomName, String avatar, Theme theme, Set<Account> members, Set<Account> admins, boolean permission) {
        super(roomName, avatar, theme);
        this.members = members;
        this.admins = admins;
        this.permission = permission;
    }

}
