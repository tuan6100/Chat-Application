package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import org.springframework.stereotype.Service;

@Service
public interface PrivateChatService extends ChatService {

    public PrivateChat findPrivateChat(Long chatId) throws ChatException;

    public void createPrivateChat(Relationship relationship);

    public PrivateChat findPrivateChatByRelationship(Long relationshipId) throws ChatException;

    public void removePrivateChat(Long chatId);

    public void removePrivateChat(Long firstAccountId, Long secondAccountId);
}
