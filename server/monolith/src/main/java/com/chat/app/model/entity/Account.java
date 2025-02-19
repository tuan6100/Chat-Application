package com.chat.app.model.entity;

import com.chat.app.enumeration.UserStatus;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.notification.FriendNotification;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.*;

@Getter
@Setter
@Entity
@Table( name="account",
        indexes = {
                @Index(name = "account_auth_index", columnList = "email, password"),
        }
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "username", nullable = false)
    protected String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "gender")
    private Character gender;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar")
    private String avatar;

    private UserStatus status;

    @OneToMany(mappedBy = "firstAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relationship> firstRelationships = new ArrayList<>();

    @OneToMany(mappedBy = "secondAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relationship> secondRelationships = new ArrayList<>();

    @ManyToMany(mappedBy = "admins", fetch = FetchType.LAZY, targetEntity = GroupChat.class)
    @ToString.Exclude
    @JsonIgnore
    private List<GroupChat> adminOfGroup = new ArrayList<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY, targetEntity = GroupChat.class)
    @ToString.Exclude
    @JsonIgnore
    private List<GroupChat> memberOfGroup = new ArrayList<>();

    @ManyToMany(mappedBy = "viewers", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Message> viewedMessages = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "senderAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FriendNotification> sentNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "receiverAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Notification> receivedNotifications = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<MessageReaction> reactions = new ArrayList<>();


    public Account() {
    }

    public Account(Long accountId, String username) {
        this.accountId = accountId;
        this.username = username;
    }
}

