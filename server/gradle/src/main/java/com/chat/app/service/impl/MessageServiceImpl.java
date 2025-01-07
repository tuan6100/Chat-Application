package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.ChatMessageRequest;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.repository.jpa.MessageRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import com.chat.app.service.redis.MessageCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    @Lazy
    private ChatService chatService;

    @Autowired
    @Lazy
    private AccountService accountService;

    @Autowired
    @Lazy
    private MessageCacheService messageCacheService;

    @Autowired
    private KafkaTemplate<String, ChatMessageRequest> kafkaTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @Override
    public Message getMessage(Long id) throws ChatException {
        return messageRepository.findById(id).
                orElseThrow(() -> new ChatException("Message not found"));
    }

    @Override
    @Async
    public void sendMessage(Long chatId, MessageRequest messageRequest)  {
        kafkaTemplate.send("send-message", new ChatMessageRequest(chatId, messageRequest));
    }

    @Override
//    @Async
    public void markViewedMessage(Long messageId, long viewerId) throws ChatException {
        Message message = getMessage(messageId);
        Account viewer = accountService.getAccount(viewerId);
        message.getViewers().add(viewer);
        messageRepository.save(message);
    }

    @Override
    @Async
    public void editMessage(Long messageId, MessageRequest messageRequest) throws ChatException {
        Long chatId = getMessage(messageId).getChat().getChatId();
        kafkaTemplate.send("edit-message", new ChatMessageRequest(chatId, messageRequest));
    }

    @Override
    @Async
    public void unsendMessage(Long messageId)  {
        kafkaTemplate.send("unsend-message", String.valueOf(messageId), null);
    }

    @Override
    @Async
    public void restoreMessage(Long messageId) {
        kafkaTemplate.send("restore-message", String.valueOf(messageId), null);
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
            if (TimeUnit.MILLISECONDS.toHours(now.getTime() - message.getSentTime().getTime()) >= 1) {
                removeMessage(message.getMessageId());
                System.out.println("Removed unsent message: " + message.getMessageId());
            }
        }
    }

}
