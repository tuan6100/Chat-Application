package com.chat.app.service.kafka;

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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MessageConsumerService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    @Lazy
    private ChatService chatService;

    @Autowired
    @Lazy
    private AccountService accountService;


    @KafkaListener(topics = "chat-message", groupId = "chat-group")
    public void consumeMessage(Long chatId, MessageRequest messageRequest) throws ChatException {
        Chat chat = chatService.getChat(chatId);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        MessageType type = MessageType.valueOf(String.valueOf(messageRequest.getType()));
        Date date = new Date();
        Message message = new Message(sender, messageRequest.getContent(), type, date, chat);
        chatService.addMessage(chat.getChatId(), message);
        messageRepository.save(message);
    }

    @KafkaListener(topics = "reply-message", groupId = "chat-group")
    public void consumeReplyMessage(Long chatId, MessageRequest messageRequest) throws ChatException {
        Chat chat = chatService.getChat(chatId);
        Account sender = accountService.getAccount(messageRequest.getSenderId());
        MessageType type = MessageType.valueOf(String.valueOf(messageRequest.getType()));
        Date date = new Date();
        Message repliedMessage = messageService.getMessage(messageRequest.getRepliedMessageId());
        Message newMessage = new Message(sender, messageRequest.getContent(), type, date, chat);
        newMessage.getRepliedMessages().add(repliedMessage);
        chatService.addMessage(chat.getChatId(), newMessage);
        messageRepository.save(newMessage);
    }

    @KafkaListener(topics = "edit-message", groupId = "chat-group")
    public void consumeEditMessage(Long messageId, MessageRequest messageRequest) throws ChatException {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ChatException("Message not found"));
        chatService.removeMessage(messageId);
        message.setContent(messageRequest.getContent());
        message.setType(MessageType.valueOf(String.valueOf(messageRequest.getType())));
        message.setSendingTime(new Date());
        chatService.addMessage(message.getChat().getChatId(), message);
        messageRepository.save(message);
    }

    @KafkaListener(topics = "unsend-message", groupId = "chat-group")
    public void consumeUnsendMessage(String messageId) throws ChatException {
        Message message = messageService.getMessage(Long.valueOf(messageId));
        message.setUnsend(true);
        chatService.removeMessage(Long.valueOf(messageId));
        messageRepository.save(message);
    }

    @KafkaListener(topics = "restore-message", groupId = "chat-group")
    public void consumeRestoreMessage(String messageId) throws ChatException {
        Message message = messageService.getMessage(Long.valueOf(messageId));
        message.setUnsend(false);
        chatService.addMessage(message.getChat().getChatId(), message);
        messageRepository.save(message);
    }
}
