package com.chat.app.model.entity.extend.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Relationship;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "private_chat")
public class PrivateChat extends Chat {

    @OneToOne (cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    public PrivateChat() {
        super();
    }

    public PrivateChat(Theme theme, Relationship relationship) {
        super(theme);
        this.relationship = relationship;
    }
}
