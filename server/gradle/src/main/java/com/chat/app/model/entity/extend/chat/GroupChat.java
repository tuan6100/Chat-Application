package com.chat.app.model.entity.extend.chat;

import com.chat.app.enumeration.GroupPermission;
import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "group_chat")
public class GroupChat extends Chat {

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_avatar")
    private String groupAvatar;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id")
    private Account creator;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "admins_in_group",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private HashSet<Account> admins;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "members_in_group",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private HashSet<Account> members;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private GroupPermission permission;


    public GroupChat() {
    }

    public GroupChat(String groupName, String avatar, Account creator, Date createdAt, GroupPermission permission) {
        super(Theme.SYSTEM);
        this.groupName = groupName;
        this.creator = creator;
        this.createdDate = createdAt;
        this.permission = permission;
        this.admins = new HashSet<>();
        this.members = new HashSet<>();

    }

}

