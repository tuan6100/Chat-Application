package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.payload.request.MessageSeenRequest;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

     Message getMessage(Long messageId) throws ChatException;

     void sendMessage(Long chatId, MessageRequest request) ;

     void markViewedMessage(Long chatId, MessageSeenRequest request) throws ChatException;

     void editMessage( Long messageId, MessageRequest request) throws ChatException;
    
     void unsendMessage(Long messageId) ;

     void restoreMessage(Long messageId) throws ChatException;

     void removeMessage(Long messageId);

}
