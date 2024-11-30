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

    @Column(name = "chat_name")
    protected String chatName;

    @Column(name = "avatar")
    protected String avatar;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<Message> messages;

    @Column(name = "theme", columnDefinition = "smallint default 2")
    protected Theme theme;

    public Chat() {
    }

    public Chat(String chatName, String avatar, Theme theme) {
        this.chatName = chatName;
        this.avatar = avatar;
        this.theme = theme;
    }

}
