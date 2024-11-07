package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "message_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Account sender;

    @Column(nullable = false)
    private Date timestamp = new Date();

    @ManyToOne
    @JoinColumn(name = "replied_message_id")
    private Message repliedMessage;
}