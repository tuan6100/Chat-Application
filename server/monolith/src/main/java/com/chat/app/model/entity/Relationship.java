package com.chat.app.model.entity;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table( name = "relationship",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"first_account_id", "second_account_id"})},
        indexes = {@Index(name = "idx_first_second_account", columnList = "first_account_id, second_account_id", unique = true)}
)
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "first_account_id", referencedColumnName = "account_id")
    @JsonIgnore
    private Account firstAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_account_id", referencedColumnName = "account_id")
    @JsonIgnore
    private Account secondAccount;

    @Enumerated(EnumType.STRING)
    private RelationshipStatus status;

    @Column(name = "timeline", nullable = false)
    private Date timeline;


    public Relationship() {
    }

    public Relationship(Account firstAccount, Account secondAccount, RelationshipStatus status, Date timeline) {
        this.firstAccount = firstAccount;
        this.secondAccount = secondAccount;
        this.status = status;
        this.timeline = timeline;
    }
}

