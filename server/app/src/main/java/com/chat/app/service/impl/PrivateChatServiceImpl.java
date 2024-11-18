package com.chat.app.service.impl;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.repository.PrivateChatRepository;
import com.chat.app.service.PrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateChatServiceImpl extends ChatServiceImpl implements PrivateChatService {

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Override
    public PrivateChat findPrivateChat(Long chatId) throws ChatException {
        return (PrivateChat) privateChatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("Private chat not found"));

    }

    @Override
    public void createPrivateChat(Relationship relationship) {
        privateChatRepository.save(new PrivateChat(relationship.getFirstAccount().getUsername(),
                                        relationship.getFirstAccount().getAvatar(),
                                        Theme.SYSTEM, relationship));
        privateChatRepository.save((new PrivateChat(relationship.getSecondAccount().getUsername(),
                                        relationship.getSecondAccount().getAvatar(),
                                        Theme.SYSTEM, relationship)));
    }

    @Override
    public PrivateChat findPrivateChatByRelationship(Long relationshipId) throws ChatException {
        PrivateChat privateChat =   privateChatRepository.findByRelationshipId(relationshipId);
        if (privateChat == null) {
            throw new ChatException("Private chat not found");
        }
        return privateChat;
    }

    @Override
    public void removePrivateChat(Long chatId) {
        privateChatRepository.deleteById(chatId);
    }

    @Override
    public void removePrivateChat(Long firstAccountId, Long secondAccountId) {
        List<PrivateChat> privateChats = privateChatRepository.findByFirstAccountIdAndSecondAccountId(firstAccountId, secondAccountId);
        privateChatRepository.deleteAll(privateChats);
    }
}
