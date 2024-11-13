package com.chat.app.model.entity.extend.chatroom;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.ChatRoom;
import com.chat.app.model.entity.Relationship;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "friendship_chat")
public class PrivateChat extends ChatRoom {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "relationshipId")
    private Relationship relationship;

    public PrivateChat() {
        super();
    }

    public PrivateChat(String roomName, String avatar, Theme theme, Relationship relationship) {
        super(roomName, avatar, theme);
        this.relationship = relationship;
    }
}
