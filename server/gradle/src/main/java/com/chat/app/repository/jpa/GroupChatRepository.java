package com.chat.app.repository.jpa;

import com.chat.app.model.entity.extend.chat.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {


}
