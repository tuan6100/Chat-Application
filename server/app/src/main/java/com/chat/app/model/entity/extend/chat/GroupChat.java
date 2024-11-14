package com.chat.app.model.entity.extend.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "group_chat")
public class GroupChat extends Chat {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id")
    private Account creator;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "admins_in_group",
            joinColumns = @JoinColumn(name = "chatId"),
            inverseJoinColumns = @JoinColumn(name = "accountId")
    )
    private HashSet<Account> admins;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "members_in_group",
            joinColumns = @JoinColumn(name = "chatId"),
            inverseJoinColumns = @JoinColumn(name = "accountId")
    )
    private HashSet<Account> members;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "permission", columnDefinition = "boolean default false")
    private boolean permission;

    public GroupChat() {
    }

    public GroupChat(String roomName, String avatar, Account creator, Date createdAt, boolean permission) {
        super(roomName, avatar, Theme.SYSTEM);
        this.creator = creator;
        this.createdAt = createdAt;
        this.permission = permission;
    }

    public boolean getPermission() {
        return permission;
    }
}

