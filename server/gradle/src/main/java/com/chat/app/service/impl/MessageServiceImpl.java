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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private KafkaTemplate<String, MessageRequest> kafkaTemplate;

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
    public Message sendMessage(Long chatId, MessageRequest messageRequest)  {
        kafkaTemplate.send("chat-message", String.valueOf(chatId), messageRequest);
        return new Message();
    }

    @Override
    public Message markViewedMessage(Long messageId, long viewerId) throws ChatException {
        Message message = getMessage(messageId);
        Account viewer = accountService.getAccount(viewerId);
        message.getViewers().add(viewer);
        return messageRepository.save(message);
    }


    @Override
    public Message replyMessage(Long chatId, Long repliedMessageId, MessageRequest messageRequest)  {
        messageRequest.setRepliedMessageId(repliedMessageId);
        kafkaTemplate.send("reply-message", String.valueOf(chatId), messageRequest);
        return new Message();
    }

    @Override
    public Message editMessage(Long messageId, MessageRequest messageRequest)  {
        kafkaTemplate.send("edit-message", String.valueOf(messageId), messageRequest);
        return new Message();
    }

    @Override
    public void unsendMessage(Long messageId)  {
        kafkaTemplate.send("unsend-message", String.valueOf(messageId), null);
    }

    @Override
    public void restoreMessage(Long messageId) throws ChatException {
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
            if (TimeUnit.MILLISECONDS.toHours(now.getTime() - message.getSendingTime().getTime()) >= 1) {
                removeMessage(message.getMessageId());
                System.out.println("Removed unsent message: " + message.getMessageId());
            }
        }
    }

}
