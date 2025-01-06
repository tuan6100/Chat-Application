package com.chat.app.model.entity;

import com.chat.app.enumeration.MessageType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "message",
        indexes = {@Index(name = "idx_chat_id", columnList = "chat_id, sent_time")}
)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @Column(name = "content", columnDefinition = "TEXT")
    @Lob
    private String content;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType type;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Date sentTime = new Date();

    @Column(name = "unsend", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean unsend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "replies",
            joinColumns = @JoinColumn(name = "original_message_id", referencedColumnName = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "reply_message_id", referencedColumnName = "message_id")
    )
    private List<Message> repliedMessages = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "viewers",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "viewer_id", referencedColumnName = "account_id")
    )
    private List<Account> viewers = new ArrayList<>();


    public Message() {
    }

    public Message(Account sender, String content, MessageType type, Date date, Chat chat) {
        this.sender = sender;
        this.sentTime = date;
        this.chat = chat;
        this.content = content;
        this.type = type;
    }
}