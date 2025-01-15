package com.chat.app.repository.jpa;

import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.chatId = ?1 AND m.sentTime = " +
            "(SELECT MAX(m1.sentTime) FROM Message m1 WHERE m1.chat.chatId = ?1)")
    Message findLatestMessageByChatId(long chatId);


    @QueryHints({ @QueryHint(name = "jakarta.persistence.query.timeout", value = "1000") })
    @Query("SELECT m FROM Message m WHERE m.chat.chatId = ?1 ORDER BY m.sentTime DESC ")
    Page<Message> findLatestMessagesByChatId(long chatId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.chatId = ?1")
    int countMessagesByChatId(long chatId);

}
