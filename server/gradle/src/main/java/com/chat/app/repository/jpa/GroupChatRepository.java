package com.chat.app.repository.jpa;

import com.chat.app.model.entity.extend.chat.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRepository extends JpaRepository<GroupChat, Long> {

    @Query("SELECT g.chatId FROM GroupChat g INNER JOIN g.members m WHERE m.accountId = ?1")
    List<Long> findByMemberId(Long memberId);

}
