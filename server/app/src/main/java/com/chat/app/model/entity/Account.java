package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;

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

    @Column(name = "bio")
    private String bio;

    @Column(name = "avatar")
    private String avatarImagePath;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Relationship> users;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Relationship> friends;
}
