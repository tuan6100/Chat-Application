package com.chat.app.model.entity;

import com.chat.app.enumeration.Theme;
import com.chat.app.repository.ChatRoomRepository;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "chat_room")
public abstract class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long roomId;

    @Column(name = "room_name")
    protected String roomName;

    @Column(name = "avatar")
    protected String avatar;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<Message> messages;

    @Column(name = "theme", columnDefinition = "varchar(255) default 'SYSTEM'")
    protected Theme theme;

    public ChatRoom() {
    }

    public ChatRoom(String roomName, String avatar, Theme theme) {
        this.roomName = roomName;
        this.avatar = avatar;
        this.theme = theme;
    }

}
