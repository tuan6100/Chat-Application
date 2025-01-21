package com.chat.app.repository.jpa;


import com.chat.app.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.unsent = true")
    List<Message> findAllByUnsendTrue();

    Message findByRandomId(String randomId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.chatId = ?1")
    int countMessagesByChatId(long chatId);
}
