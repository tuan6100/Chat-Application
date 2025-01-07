package com.chat.app.service;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.extend.chat.SpamChat;
import org.springframework.stereotype.Service;

@Service
public interface SpamChatService  extends ChatService {

    SpamChat getSpamChat(Long senderId, Long receiverId);

    SpamChat create(Theme theme, Long SenderId, Long ReceiverId) throws ChatException;

    void removeSpamChats();

}
