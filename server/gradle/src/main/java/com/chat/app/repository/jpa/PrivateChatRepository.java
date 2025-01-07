package com.chat.app.repository.jpa;


import com.chat.app.model.entity.extend.chat.PrivateChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateChatRepository extends JpaRepository<PrivateChat, Long> {

    @Query("SELECT c.chatId FROM PrivateChat c WHERE c.relationship.relationshipId = ?1")
    Long findByRelationshipId(Long relationshipId);

    @Query("SELECT c.chatId FROM PrivateChat c " +
            "INNER JOIN c.relationship r ON c.relationship.relationshipId = r.relationshipId " +
            "WHERE r.firstAccount.accountId = ?1 OR r.secondAccount.accountId = ?1")
    List<Long> findChatsByAccountId(Long accountId);

}
