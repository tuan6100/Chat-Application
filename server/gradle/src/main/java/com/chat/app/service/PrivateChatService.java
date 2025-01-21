package com.chat.app.service;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PrivateChatService extends ChatService {

    PrivateChat create(Theme theme, Long RelationshipId) throws ChatException;

    List<Long> findAllPrivateChatsByAccountId(Long accountId);

    Long getByRelationshipId(Long relationshipId);

    void remove(Long chatId);
}
