package com.chat.app.model.entity;

import com.chat.app.enumeration.MessageType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table( name = "message",
        indexes = { @Index(name = "idx_chat_id", columnList = "chat_id"),
                    @Index(name = "idx_sent_time", columnList = "sent_time"),
                    @Index(name = "idx_type", columnList = "type"),
                    @Index(name = "idx_unsent", columnList = "unsent"),
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    protected Long messageId;

    @Column(name = "random_id", unique = true)
    protected String randomId;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    protected Account sender;

    @Column(name = "content", columnDefinition = "TEXT")
    @Lob
    protected String content;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    protected MessageType type;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    protected Date sentTime = new Date();

    @Column(name = "pinned", columnDefinition = "BOOLEAN DEFAULT FALSE")
    protected Boolean pinned = false;

    @Column(name = "unsent", columnDefinition = "BOOLEAN DEFAULT FALSE")
    protected Boolean unsent = false;

    @Column(columnDefinition = "TIMESTAMP")
    protected Date deletedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    protected Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id", foreignKey = @ForeignKey(name = "fk_reply_to_message"))
    @OnDelete(action = OnDeleteAction.SET_NULL)
    protected Message replyTo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "viewers",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "viewer_id", referencedColumnName = "account_id")
    )
    protected List<Account> viewers = new ArrayList<>();

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<MessageReaction> reactions = new ArrayList<>();


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