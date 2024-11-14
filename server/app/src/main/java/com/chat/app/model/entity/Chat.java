package com.chat.app.model.entity;

import com.chat.app.enumeration.Theme;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "chat_room")
public abstract class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long chatId;

    @Column(name = "room_name")
    protected String roomName;

    @Column(name = "avatar")
    protected String avatar;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<Message> messages;

    @Column(name = "theme", columnDefinition = "varchar(255) default 'SYSTEM'")
    protected Theme theme;

    public Chat() {
    }

    public Chat(String roomName, String avatar, Theme theme) {
        this.roomName = roomName;
        this.avatar = avatar;
        this.theme = theme;
    }

}
