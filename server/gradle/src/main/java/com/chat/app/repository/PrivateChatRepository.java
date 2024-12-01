package com.chat.app.repository;

import com.chat.app.model.entity.extend.chat.PrivateChat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivateChatRepository extends ChatRepository{

    @Query("SELECT c FROM PrivateChat c WHERE c.relationship.relationshipId = ?1")
    public PrivateChat findByRelationshipId(Long relationshipId);

    @Query("SELECT c FROM PrivateChat c " +
            "WHERE c.relationship.firstAccount.accountId = ?1 AND c.relationship.secondAccount.accountId = ?2 " +
            "OR c.relationship.firstAccount.accountId = ?2 AND c.relationship.secondAccount.accountId = ?1")
    public List<PrivateChat> findByFirstAccountIdAndSecondAccountId(Long firstAccountId, Long secondAccountId);
}
