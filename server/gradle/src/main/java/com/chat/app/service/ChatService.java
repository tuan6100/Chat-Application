package com.chat.app.service;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
 public interface ChatService {

     Chat getChat(Long chatId) throws ChatException;

     Page<Message> getMessages(Long chatId, Pageable pageable) throws ChatException;

     Chat addMessage(Long chatId, Message message) throws ChatException;

     Chat pinMessage(Long messageId) throws ChatException;

     Chat removeMessage(Long MessageId) throws ChatException;

     Chat changeTheme(Long chatId, Theme theme) throws ChatException;


}
