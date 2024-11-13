package com.chat.app.model.entity.extend.chatroom;

import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.ChatRoom;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "group_chat")
public class GroupChat extends ChatRoom {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator_id")
    private Account creator;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "admins_in_group",
            joinColumns = @JoinColumn(name = "roomId"),
            inverseJoinColumns = @JoinColumn(name = "accountId")
    )
    private HashSet<Account> admins;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "members_in_group",
            joinColumns = @JoinColumn(name = "roomId"),
            inverseJoinColumns = @JoinColumn(name = "accountId")
    )
    private HashSet<Account> members;

    @Column
    private Date createdAt;
}

