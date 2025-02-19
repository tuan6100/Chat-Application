package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long notificationId;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Account receiverAccount;

    @Column(name = "content", nullable = false)
    protected String content;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date sentDate;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date viewedDate;


    public Notification() {}

    public Notification(Account receiverAccount, String content, Date sentDate) {
        this.receiverAccount = receiverAccount;
        this.content = content;
        this.sentDate = sentDate;
    }
}
