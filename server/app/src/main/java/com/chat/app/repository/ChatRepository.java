package com.chat.app.repository;

import com.chat.app.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Chat findBychatId(Long chatId);

    Chat findByRoomName(String roomName) ;

}
