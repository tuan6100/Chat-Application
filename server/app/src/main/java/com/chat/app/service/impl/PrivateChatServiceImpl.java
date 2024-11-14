package com.chat.app.service.impl;

import com.chat.app.enumeration.Theme;
import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.repository.PrivateChatRepository;
import com.chat.app.service.PrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrivateChatServiceImpl extends ChatServiceImpl implements PrivateChatService {

    @Autowired
    private PrivateChatRepository privateChatRepository;

    @Override
    public void createPrivateChat(Relationship relationship) {
        privateChatRepository.save(new PrivateChat(relationship.getFriend().getUsername(), relationship.getFriend().getAvatar(), Theme.SYSTEM, relationship));
    }

    @Override
    public void removePrivateChat(Long chatId) {
        privateChatRepository.deleteById(chatId);
    }
}
