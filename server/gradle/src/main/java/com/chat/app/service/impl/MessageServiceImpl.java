package com.chat.app.service.impl;

import com.chat.app.enumeration.MessageType;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.repository.jpa.MessageRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    public Message getMessage(Long id) throws ChatException {
        return messageRepository.findById(id).
                orElseThrow(() -> new ChatException("Message not found"));
    }

    @Override
    public Message storeMessage(MessageRequest messageRequest) throws ChatException {
        Chat chat = chatService.getChat(messageRequest.getChatId());
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        String content = messageRequest.getContent();
        MessageType type = MessageType.valueOf(String.valueOf(messageRequest.getType()));
        Date date = new Date();
        Message message = new Message(sender, content, type, date, chat);
        chatService.addMessage(chat.getChatId(), message);
        return messageRepository.save(message);
    }

    @Override
    public Message viewMessage(Long messageId, long viewerId) throws ChatException {
        Message message = getMessage(messageId);
        Account viewer = accountService.getAccount(viewerId);
        message.getViewers().add(viewer);
        return messageRepository.save(message);
    }


    @Override
    public Message replyMessage(Long chatId, Long repliedMessageId, MessageRequest messageRequest) throws ChatException {
        Chat chat = chatService.getChat(messageRequest.getChatId());
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        String content = messageRequest.getContent();
        MessageType type = MessageType.valueOf(String.valueOf(messageRequest.getType()));
        Date date = new Date();
        Message newMessage = new Message(sender, content, type, date, chat);
        Message repliedMessage = getMessage(repliedMessageId);
        newMessage.getRepliedMessages().add(repliedMessage);
        chatService.addMessage(chat.getChatId(), newMessage);
        return messageRepository.save(newMessage);
    }

    @Override
    public Message editMessage(Long messageId, MessageRequest messageRequest) throws ChatException {
        Message message = getMessage(messageId);
        chatService.removeMessage(messageId);
        message.setContent(messageRequest.getContent());
        message.setType(MessageType.valueOf(String.valueOf(messageRequest.getType())));
        message.setSendingTime(new Date());
        chatService.addMessage(message.getChat().getChatId(), message);
        return messageRepository.save(message);
    }

    @Override
    public void unsendMessage(Long messageId) throws ChatException {
        Message message = getMessage(messageId);
        message.setUnsend(true);
        chatService.removeMessage(messageId);
        messageRepository.save(message);
    }

    @Override
    public void restoreMessage(Long messageId) throws ChatException {
        Message message = getMessage(messageId);
        message.setUnsend(false);
        messageRepository.save(message);
    }

    @Override
    public void removeMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void removeUnsentMessages() {
        Date now = new Date();
        List<Message> unsentMessages = messageRepository.findAllByUnsendTrue();
        for (Message message : unsentMessages) {
            if (TimeUnit.MILLISECONDS.toHours(now.getTime() - message.getSendingTime().getTime()) >= 1) {
                removeMessage(message.getMessageId());
                System.out.println("Removed unsent message: " + message.getMessageId());
            }
        }
    }

}
