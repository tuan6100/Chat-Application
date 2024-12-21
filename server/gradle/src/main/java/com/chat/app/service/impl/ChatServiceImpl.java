package com.chat.app.service.impl;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.repository.jpa.ChatRepository;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Primary
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    @Lazy
    private MessageService messageService;


    @Override
    public Chat findChat(Long chatId) throws ChatException {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("Chat not found"));
    }

    @Override
    public Chat addMessage(Long chatId, Long MessageId) throws ChatException {
        Chat chat = findChat(chatId);
        Message message = messageService.findMessage(MessageId);
        chat.getMessages().add(message);
        return chatRepository.save(chat);
    }

    @Override
    public Chat removeMessage(Long chatId, Long MessageID) throws ChatException {
        Chat chat = findChat(chatId);
        Message message = messageService.findMessage(MessageID);
        chat.getMessages().remove(message);
        return chatRepository.save(chat);
    }

    @Override
    public Chat changeTheme(Long chatId, Theme theme) throws ChatException {
        Chat chat = findChat(chatId);
        chat.setTheme(theme);
        return chatRepository.save(chat);
    }

    @Override
    public Message findMessage(long chatId, Long messageId) throws ChatException {
        Chat chat = findChat(chatId);
        return chat.getMessages().stream()
                .filter(message -> Objects.equals(message.getMessageId(), messageId))
                .findFirst()
                .orElseThrow(() -> new ChatException("Message not found in chat room"));
    }


}
