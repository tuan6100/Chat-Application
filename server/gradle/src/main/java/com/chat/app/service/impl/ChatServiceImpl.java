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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    @Lazy
    private MessageService messageService;


    @Override
    public Chat getChat(Long chatId) throws ChatException {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatException("Chat not found"));
    }

    @Override
    public Page<Message> getMessages(Long chatId, Pageable pageable) throws ChatException {
        return chatRepository.findMessagesByChatId(chatId, pageable);

    }

    @Override
    public Chat addMessage(Long chatId, Message message) throws ChatException {
        Chat chat = getChat(chatId);
        chat.getMessages().add(message);
        return chatRepository.save(chat);
    }

    @Override
    public Chat pinMessage(Long messageId) throws ChatException {
        Message message = messageService.getMessage(messageId);
        Chat chat = message.getChat();
        chat.getPinnedMessages().add(message);
        return chatRepository.save(chat);
    }

    @Override
    public Chat removeMessage(Long messageId) throws ChatException {
        Message message = messageService.getMessage(messageId);
        Chat chat = message.getChat();
        chat.getMessages().remove(message);
        return chatRepository.save(chat);
    }

    @Override
    public Chat changeTheme(Long chatId, Theme theme) throws ChatException {
        Chat chat = getChat(chatId);
        chat.setTheme(theme);
        return chatRepository.save(chat);
    }




}
