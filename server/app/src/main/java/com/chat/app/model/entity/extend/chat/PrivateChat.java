package com.chat.app.model.entity.extend.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Relationship;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "private_chat")
public class PrivateChat extends Chat {

    @ManyToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    public PrivateChat() {
        super();
    }

    public PrivateChat(String chatName, String avatar, Theme theme, Relationship relationship) {
        super(chatName, avatar, theme);
        this.relationship = relationship;
    }
}
