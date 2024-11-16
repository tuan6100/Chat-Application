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
@Table(name = "friendship_chat")
public class PrivateChat extends Chat {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    public PrivateChat() {
        super();
    }

    public PrivateChat(String roomName, String avatar, Theme theme, Relationship relationship) {
        super(roomName, avatar, theme);
        this.relationship = relationship;
    }
}
