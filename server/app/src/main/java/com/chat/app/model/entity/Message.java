package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "message_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    protected Account sender;

    @Column(nullable = false)
    protected Date timestamp = new Date();

//    @Column(name = "reactions")
//    protected HashMap<String, String> reactions;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "chatId")
    protected Chat chat;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "replies",
            joinColumns = @JoinColumn(name = "original_message_id", referencedColumnName = "messageId"),
            inverseJoinColumns = @JoinColumn(name = "reply_message_id", referencedColumnName = "messageId")
    )
    protected List<Message> repliedMessages;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "views",
            joinColumns = @JoinColumn(name = "messageId"),
            inverseJoinColumns = @JoinColumn( name = "accountId")
    )
    protected List<Account> viewers;


    public Message() {
    }

    public Message(Account sender, Date date, Chat chat, List<Message> replies) {
        this.sender = sender;
        this.timestamp = date;
        this.chat = chat;
        this.repliedMessages = replies;
    }
}