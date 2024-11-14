package com.chat.app.model.entity;

import com.chat.app.enumeration.UserStatus;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Data
@Entity
@Table(name="account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birthDate")
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
}
