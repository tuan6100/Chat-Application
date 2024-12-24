package com.chat.app.model.entity;

import com.chat.app.enumeration.Theme;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "chat")
public abstract class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long chatId;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<Message> pinnedMessages = new ArrayList<>();

    @Column(name = "theme")
    @Enumerated(EnumType.STRING)
    protected Theme theme;


    public Chat() {
    }

    public Chat(Theme theme) {
        this.theme = theme;
    }

}
