package com.chat.app.model.entity.extend.chatroom;

import com.chat.app.model.entity.ChatRoom;
import com.chat.app.model.entity.Relationship;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "friendship_chat")
public class FriendshipChat extends ChatRoom {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "relationshipId")
    private Relationship relationship;
}
