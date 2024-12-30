package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

     Message getMessage(Long messageId) throws ChatException;

     Message sendMessage(Long chatId, MessageRequest request) ;

     Message markViewedMessage(Long messageId, long viewedId) throws ChatException;

     Message replyMessage(Long chatId, Long repliedMessageId, MessageRequest request) ;

     Message editMessage( Long messageId, MessageRequest request) ;
    
     void unsendMessage(Long messageId) ;

     void restoreMessage(Long messageId) throws ChatException;

     void removeMessage(Long messageId);
}
