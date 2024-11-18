package com.chat.app.model.entity;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "relationship")
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_account_id", referencedColumnName = "account_id")
    @JsonBackReference("first-account-relationships")
    private Account firstAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_account_id", referencedColumnName = "account_id")
    @JsonBackReference("second-account-relationships")
    private Account secondAccount;

    @Enumerated(EnumType.STRING)
    private RelationshipStatus status;

    @Column(name = "timeline", nullable = false)
    private Date timeline;

    public Relationship(Account firstAccount, Account secondAccount, RelationshipStatus status, Date timeline) {
        this.firstAccount = firstAccount;
        this.secondAccount = secondAccount;
        this.status = status;
        this.timeline = timeline;
    }
}

