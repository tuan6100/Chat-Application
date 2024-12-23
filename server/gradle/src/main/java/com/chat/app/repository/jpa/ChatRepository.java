package com.chat.app.repository.jpa;

import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Chat findBychatId(Long chatId);

    @Query("SELECT c.messages FROM Chat c WHERE c.chatId = ?1")
    Page<Message> findMessagesByChatId(long chatId, Pageable pageable);

}
