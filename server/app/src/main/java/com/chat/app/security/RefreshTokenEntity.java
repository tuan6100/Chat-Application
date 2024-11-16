package com.chat.app.security;

import com.chat.app.model.entity.Account;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;


@Data
@Entity
@Table(name = "refresh_token")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", nullable = false)
    @JsonBackReference
    private Account account;

    @Column
    private boolean isMobile;

    @Column(nullable = false)
    private Instant expiryDate;

    public RefreshTokenEntity() {}

    public RefreshTokenEntity(String token, Account account, Instant expiryDate) {
        this.token = token;
        this.account = account;
        this.expiryDate = expiryDate;
    }

}

