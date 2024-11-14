package com.chat.app.service;

import com.chat.app.model.entity.Relationship;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import org.springframework.stereotype.Service;

@Service
public interface PrivateChatService extends ChatService {

    public void createPrivateChat(Relationship relationship);

    public void removePrivateChat(Long chatId);
}
