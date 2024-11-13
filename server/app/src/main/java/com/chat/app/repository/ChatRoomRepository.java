package com.chat.app.repository;

import com.chat.app.model.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByChatRoomId(Long chatRoomId);

    ChatRoom findByRoomName(String roomName);

}
