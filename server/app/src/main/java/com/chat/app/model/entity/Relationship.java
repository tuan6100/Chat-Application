package com.chat.app.model.entity;

import com.chat.app.enumeration.RelationshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
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

    public Relationship(Account user, Account friend, RelationshipStatus relationshipStatus, Date timeline) {
        this.user = user;
        this.friend = friend;
        this.status = relationshipStatus;
        this.timeline = timeline;
    }


}
