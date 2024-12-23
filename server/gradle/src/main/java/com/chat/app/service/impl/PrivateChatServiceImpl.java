package com.chat.app.service.impl;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.repository.jpa.PrivateChatRepository;
import com.chat.app.service.PrivateChatService;
import com.chat.app.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class PrivateChatServiceImpl extends ChatServiceImpl implements PrivateChatService {

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    @Lazy
    private RelationshipService relationshipService;


    @Override
    public PrivateChat create(Theme theme, Long RelationshipId) throws ChatException {
        Relationship relationship = relationshipService.getRelationship(RelationshipId);
        if (relationship == null) {
            throw new ChatException("Relationship not found");
        }
        PrivateChat privateChat = new PrivateChat(theme, relationship);
        return chatRepository.save(privateChat);
    }

    @Override
    public Long getByRelationshipId(Long relationshipId) {
        return privateChatRepository.findByRelationshipId(relationshipId);
    }

    @Override
    public void remove(Long chatId) {
        chatRepository.deleteById(chatId);
    }
}
