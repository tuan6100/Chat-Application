package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

     Message getMessage(Long messageId) throws ChatException;

     Message storeMessage(MessageRequest request) throws ChatException;

     Message viewMessage(Long messageId, long viewedId) throws ChatException;

     Message replyMessage(Long chatId, Long repliedMessageId, MessageRequest request) throws ChatException;

     Message editMessage( Long messageId, MessageRequest request) throws ChatException;
    
     void unsendMessage(Long messageId) throws ChatException;

     void restoreMessage(Long messageId) throws ChatException;

     void removeMessage(Long messageId);
}
