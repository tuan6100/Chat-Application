package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

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


    public RefreshToken() {
    }

    public RefreshToken(String token, Account account, Instant expiryDate, Instant createdDate) {
        this.token = token;
        this.account = account;
        this.expiryDate = expiryDate;
        this.createdDate = createdDate;
    }

}
