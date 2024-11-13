package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "replies",
            joinColumns = @JoinColumn(name = "original_message_id", referencedColumnName = "messageId"),
            inverseJoinColumns = @JoinColumn(name = "reply_message_id", referencedColumnName = "messageId")
    )
    protected List<Message> repliedMessage;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "roomId")
    protected ChatRoom chatRoom;
}