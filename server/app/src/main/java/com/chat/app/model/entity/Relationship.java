package com.chat.app.model.entity;

import com.chat.app.enumeration.RelationshipStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "relationship")
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long RelationshipId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "accountId")
    private Account user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "friend_id", referencedColumnName = "accountId")
    private Account friend;

    @Column(name = "status", nullable = false)
    private RelationshipStatus status;

    @Column(name = "timeline", nullable = false)
    private Date timeline = new Date();
}
