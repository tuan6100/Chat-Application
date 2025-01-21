package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.payload.request.MessageSeenRequest;
import com.chat.app.payload.request.MessageUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

     Message getMessage(Long messageId) throws ChatException;

     void sendMessage(Long chatId, MessageRequest request) ;

     void markViewedMessage(Long chatId, MessageSeenRequest request) throws ChatException;

     void updateMessage(Long chatId, MessageUpdateRequest request) throws ChatException;
    
     void unsendMessage(Long chatId, Long messageId) throws ChatException;

     void restoreMessage(Long chatId, Long messageId) throws ChatException;

     void removeMessage(Long messageId);

}
