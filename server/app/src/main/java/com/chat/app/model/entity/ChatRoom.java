package com.chat.app.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "chat_room")
public abstract class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomId;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "avatar")
    private String avatar;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Message> messages;

    @Column(name = "theme")
    private String theme;

}
