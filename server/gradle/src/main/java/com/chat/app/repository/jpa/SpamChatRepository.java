package com.chat.app.repository.jpa;

import com.chat.app.model.entity.extend.chat.SpamChat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpamChatRepository extends ChatRepository {

    @Query("SELECT c.chatId FROM SpamChat c WHERE c.sender.accountId = ?1 AND c.receiver.accountId = ?2")
    Long findSpamChatBySenderAndReceiver(Long senderId, Long receiverId);

    @Query("SELECT c.messages FROM SpamChat c WHERE c.sender.accountId = ?1 AND c.receiver.accountId = ?2")
    List<SpamChat> findSpamMessageBySenderAndReceiver(Long senderId, Long receiverId);
}
