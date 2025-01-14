package com.chat.app.model.entity;

import com.chat.app.enumeration.MessageType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table( name = "message",
        indexes = { @Index(name = "idx_chat_id", columnList = "chat_id"),
                    @Index(name = "idx_sent_time", columnList = "sent_time"),
        }
)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "random_id", unique = true)
    private String randomId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private Message replyTo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "viewers",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "viewer_id", referencedColumnName = "account_id")
    )
    private List<Account> viewers = new ArrayList<>();


    public Message() {
    }

    public Message(String randomId,Account sender, String content, MessageType type, Date date, Chat chat) {
        this.randomId = randomId;
        this.sender = sender;
        this.sentTime = date;
        this.chat = chat;
        this.content = content;
        this.type = type;
    }
}