package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "relationship")
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "accountId")
    private Account user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_id", referencedColumnName = "accountId")
    private Account friend;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "timeline", nullable = false)
    private Date timeline = new Date();
}
