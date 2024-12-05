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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column
    private boolean isMobile;

    @Column
    private Instant createdDate;

    @Column(nullable = false)
    private Instant expiryDate;

    public RefreshTokenEntity() {}

    public RefreshTokenEntity(String token, Account account, Instant expiryDate, Instant createdDate) {
        this.token = token;
        this.account = account;
        this.expiryDate = expiryDate;
        this.createdDate = createdDate;
    }

}

