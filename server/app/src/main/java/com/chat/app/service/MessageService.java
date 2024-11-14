package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

    public Message findMessage(Long messageId) throws ChatException;

    public Message filterMessage(String keyword) throws ChatException;

    public Message sendTextMessage(Long chatId, MessageRequest request) throws ChatException;

    public Message viewMessage(Long chatId, Long messageId, long viewedId) throws ChatException;

    //public Message reactMessage(Long chatId, Long messageId, long accountId, String emojiUnicode) throws AccountException;

    public Message replyMessage(Long chatId, Long repliedMessageId, MessageRequest request ) throws ChatException;

    public Message editMessage(Long chatId, Long messageId, MessageRequest request) throws ChatException;

    public void removeMessage(Long messageId) throws ChatException;
}
