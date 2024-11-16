package com.chat.app.model.entity;

import com.chat.app.enumeration.UserStatus;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.security.RefreshTokenEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name="account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "gender")
    private Character gender;

    @Column(name = "bio")
    private String bio;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "status")
    private UserStatus status;

    @OneToMany(mappedBy = "user",  fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Relationship> users;

    @OneToMany(mappedBy = "friend",  fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Relationship> friends;

    @ManyToMany(mappedBy = "admins",  fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<GroupChat> adminOfGroup;

    @ManyToMany(mappedBy = "members",  fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<GroupChat> memberOfGroup;

    @ManyToMany(mappedBy = "viewers",  fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Message> messages;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();
}
