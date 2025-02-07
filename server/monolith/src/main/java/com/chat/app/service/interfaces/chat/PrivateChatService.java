package com.chat.app.service.interfaces.chat;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.payload.response.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PrivateChatService extends ChatService {

    PrivateChat create(Theme theme, Long RelationshipId) throws ChatException;

    List<ChatResponse> getAllPrivateChatsByAccountId(Long accountId) throws ChatException;

    Long getByRelationshipId(Long relationshipId);

    void remove(Long chatId);
}
