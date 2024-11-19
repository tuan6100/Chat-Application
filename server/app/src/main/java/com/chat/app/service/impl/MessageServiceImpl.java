package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.message.FileMessage;
import com.chat.app.model.entity.extend.message.ImageMessage;
import com.chat.app.model.entity.extend.message.TextMessage;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.repository.MessageRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    @Lazy
    private ChatService chatService;

    @Autowired
    private AccountService accountService;


    @Override
    public Message findMessage(Long id) throws ChatException {
        return messageRepository.findById(id).
                orElseThrow(() -> new ChatException("Message not found"));
    }

    @Override
    public Message filterMessage(String keyword) throws ChatException {
        return null;
    }

    @Override
    public Message sendMessage(Long chatId, MessageRequest messageRequest) throws ChatException {
        Chat chat = chatService.findChat(chatId);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        String content = messageRequest.getContent();
        Date date = new Date();
        if (Objects.equals(messageRequest.getType(), "Text")) {
            Message message = new TextMessage(sender, date, chat, null, content);
            return messageRepository.save(message);
        }
        if (Objects.equals(messageRequest.getType(), "Image")) {
            Message message = new ImageMessage(sender, date, chat, null, content);
            return messageRepository.save(message);
        }
        if (Objects.equals(messageRequest.getType(), "File")) {
            Path path = Paths.get(content);
            Message message = new FileMessage(sender, date, chat, null, content,  path.getFileName().toString());
            return messageRepository.save(message);
        }
        return null;
    }

    @Override
    public Message viewMessage(Long chatId, Long messageId, long viewerId) throws ChatException {
        Message message = chatService.findMessage(chatId, messageId);
        Account viewer = accountService.getAccount(viewerId);
        message.getViewers().add(viewer);
        return messageRepository.save(message);
    }


    @Override
    public Message replyMessage(Long chatId, Long repliedMessageId, MessageRequest messageRequest) throws ChatException {
        Chat chat = chatService.findChat(chatId);
        Message repliedMessage = chatService.findMessage(chatId, repliedMessageId);
        List<Message> repliedMessages = new ArrayList<>();
        repliedMessages.add(repliedMessage);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        String content = messageRequest.getContent();
        Date date = new Date();
        if (Objects.equals(messageRequest.getType(), "Text")) {
            Message message = new TextMessage(sender, date, chat, repliedMessages, content);
            return messageRepository.save(message);
        }
        if (Objects.equals(messageRequest.getType(), "Image")) {
            Message message = new ImageMessage(sender, date, chat, repliedMessages, content);
            return messageRepository.save(message);
        }
        if (Objects.equals(messageRequest.getType(), "File")) {
            Path path = Paths.get(content);
            Message message = new FileMessage(sender, date, chat, repliedMessages, content,  path.getFileName().toString());
            return messageRepository.save(message);
        }
        return null;
    }

    @Override
    public Message editMessage(Long chatId, Long messageId, MessageRequest messageRequest) throws ChatException {
        Message message = chatService.findMessage(chatId, messageId);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        if (message instanceof TextMessage && Objects.equals(messageRequest.getType(), "Text")) {
            if (sender != message.getSender()) {
                throw new ChatException("You can not edit messages");
            }
            ((TextMessage) message).setTextContent(messageRequest.getContent());
        }
        return messageRepository.save(message);
    }


    @Override
    public void removeMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }
}
