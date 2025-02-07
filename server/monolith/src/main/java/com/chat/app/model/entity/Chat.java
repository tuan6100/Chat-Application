package com.chat.app.model.entity;

import com.chat.app.enumeration.Theme;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "chat")
public abstract class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long chatId;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<Message> messages = new ArrayList<>();

    @Column(name = "theme")
    @Enumerated(EnumType.STRING)
    protected Theme theme;


    public Chat() {
    }

    public Chat(Theme theme) {
        this.theme = theme;
    }

}
