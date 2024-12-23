package com.chat.app.repository.jpa;


import com.chat.app.model.entity.extend.chat.PrivateChat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateChatRepository extends ChatRepository{

    @Query("SELECT c.chatId FROM PrivateChat c WHERE c.relationship.relationshipId = ?1")
    Long findByRelationshipId(Long relationshipId);

}
