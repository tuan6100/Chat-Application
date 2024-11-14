package com.chat.app.service;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {

    public Chat addMessage(Long chatId, Long MessageID) throws ChatException;

    public Chat removeMessage(Long chatId, Long MessageID) throws ChatException;

    public Chat changeTheme(Long chatId, Theme theme) throws ChatException;

    public Chat findChat(Long chatId) throws ChatException;

    public Message findMessage(long chatId, Long messageId) throws ChatException;

}
