package com.chat.app.model.entity;

import com.chat.app.enumeration.UserStatus;
import com.chat.app.model.entity.extend.chatroom.GroupChat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.engine.internal.CacheHelper;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

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
    private String avatarImagePath;

    @Column(name = "status")
    private UserStatus status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HashSet<Relationship> users;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private HashSet<Relationship> friends;

    @ManyToMany(mappedBy = "admins", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private HashSet<GroupChat> adminOfGroup;

    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private HashSet<GroupChat> memberOfGroup;
}
