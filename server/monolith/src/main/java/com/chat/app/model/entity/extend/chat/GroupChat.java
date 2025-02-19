package com.chat.app.model.entity.extend.chat;

import com.chat.app.enumeration.GroupPermission;
import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.extend.notification.GroupNotification;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
            name = "admins_of_group",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> admins = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "members_in_group",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private List<Account> members = new ArrayList<>();

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "permission")
    @Enumerated(EnumType.STRING)
    private GroupPermission permission;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupNotification> sentNotifications = new ArrayList<>();


    public GroupChat() {
    }

    public GroupChat(String groupName, String avatar, Account creator, Date createdAt, GroupPermission permission) {
        super(Theme.SYSTEM);
        this.groupName = groupName;
        this.creator = creator;
        this.createdDate = createdAt;
        this.permission = permission;
        this.admins.add(creator);
        this.members.add(creator);
        members.add(creator);
    }

}

