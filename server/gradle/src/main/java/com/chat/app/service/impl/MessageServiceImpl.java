package com.chat.app.service.impl;

import com.chat.app.enumeration.MessageType;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.MessageReaction;
import com.chat.app.model.entity.extend.message.CallMessage;
import com.chat.app.payload.request.*;
import com.chat.app.payload.response.MessageCallResponse;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.jpa.MessageRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import com.chat.app.service.aws.S3Service;
import com.chat.app.service.elasticsearch.MessageSearchService;
import com.chat.app.service.redis.MessageCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    @Lazy
    private MessageSearchService messageSearchService;

    @Autowired
    @Lazy
    private S3Service s3Service;


    @Override
    public Message getMessage(Long id) throws ChatException {
        return messageRepository.findById(id).
                orElseThrow(() -> new ChatException("Message not found"));
    }

    @Override
    public void sendMessage(Long chatId, MessageRequest messageRequest)  {
        kafkaTemplate.send("send-message", new ChatMessageRequest(chatId, messageRequest, 0));
    }

    @Override
    public void sendMessage(Long chatId, MessageCallRequest request) throws ChatException {
        Chat chat = chatService.getChat(chatId);
        Account sender = accountService.getAccount(request.getSenderId());
        MessageType type = MessageType.valueOf(request.getType().toUpperCase());
        Date sentTime = request.getSentTime();
        CallMessage message = new CallMessage(request.getRandomId(), sender, request.getContent(), type, sentTime, chat, request.getOffer());
        if (request.getReplyToMessageId() != null) {
            Message replyToMessage = getMessage(request.getReplyToMessageId());
            message.setReplyTo(replyToMessage);
        }
        message = messageRepository.save(message);
        MessageCallResponse response = MessageCallResponse.fromEntity(message);
        messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/send", response);
    }

    @Override
    @Transactional
    public void markViewedMessage(Long chatId, MessageSeenRequest request) throws ChatException {
        Message message = getMessage(request.getMessageId());
        if (message.getSender().getAccountId().equals(request.getViewerId())) {
            return;
        }
        Account viewer = accountService.getAccount(request.getViewerId());
        if (message.getViewers().contains(viewer)) {
            return;
        }
        message.getViewers().add(viewer);
        message = messageRepository.save(message);
        MessageResponse messageResponse = MessageResponse.fromEntity(message);
        System.out.println("Marked message as seen: " + messageResponse.getContent());
        messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/mark-seen", messageResponse);
        List<Long> membersInChat = chatService.getAllMembersInChat(chatId);
        messageCacheService.cacheNewMessage(chatId, membersInChat, messageResponse);
    }

    @Override
    @Transactional
    public void updateMessage(Long chatId, MessageUpdateRequest request) throws ChatException {
        System.out.println("Updating message: " + request.toString());
        if (request.getMessageId() == null) {
            throw new ChatException("Message id is required");
        }
        Message message = getMessage(request.getMessageId());
        MessageResponse messageResponse = new MessageResponse();
        if (request.getContent() != null) {
            message.setContent(request.getContent());
            messageSearchService.updateMessage(message.getMessageId(), request.getContent());
            messageResponse = MessageResponse.fromEntity(message);
            messageResponse.setStatus("edited");
        }
        if (request.getReaction() != null) {
            System.out.println("Updating message: " + request.getMessageId() + " with reaction: " + request.getReaction());
            Account account = accountService.getAccount(request.getAccountId());
            if (message.getReactions().stream().anyMatch(reaction -> reaction.getAccount().getAccountId().equals(request.getAccountId()))) {
                if (message.getReactions().stream().anyMatch(reaction -> reaction.getReaction().equals(request.getReaction()))) {
                    message.getReactions().removeIf(reaction -> reaction.getAccount().getAccountId().equals(request.getAccountId()));
                } else {
                    message.getReactions().removeIf(reaction -> reaction.getAccount().getAccountId().equals(request.getAccountId()));
                    MessageReaction newReaction = new MessageReaction(message, account, request.getReaction());
                    message.getReactions().add(newReaction);
                }
            } else {
                MessageReaction reaction = new MessageReaction(message, account, request.getReaction());
                message.getReactions().add(reaction);
            }
            messageResponse = MessageResponse.fromEntity(message);
        }
        messageRepository.save(message);
        messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/update", messageResponse);
        System.out.println("Sent message: " + messageResponse.getContent());
        List<Long> membersInChat = chatService.getAllMembersInChat(chatId);
        messageCacheService.cacheNewMessage(chatId, membersInChat, messageResponse);
    }

    @Override
    @Transactional
    public void unsendMessage(Long chatId, Long messageId) throws ChatException {
        Message message = getMessage(messageId);
        message.setUnsent(true);
        message.setDeletedTime(new Date());
        message = messageRepository.save(message);
        messageSearchService.deleteMessage(messageId);
        MessageResponse messageResponse = MessageResponse.fromEntity(message);
        messageResponse.setStatus("deleted");
        messageResponse.setContent("");
        messageResponse.setType("TEXT");
        messageResponse.setReactions(null);
        messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/delete", messageResponse);
        System.out.println("Deleted message: " + messageResponse.getMessageId());
        List<Long> membersInChat = chatService.getAllMembersInChat(chatId);
        messageCacheService.cacheNewMessage(chatId, membersInChat, messageResponse);
    }

    @Override
    @Transactional
    public void restoreMessage(Long chatId, Long messageId) throws ChatException {
        Message message = getMessage(messageId);
        message.setUnsent(false);
        message.setDeletedTime(null);
        messageRepository.save(message);
        messageSearchService.addNewMessage(message);
        List<Long> membersInChat = chatService.getAllMembersInChat(chatId);
        MessageResponse messageResponse = MessageResponse.fromEntity(message);
        messageCacheService.cacheNewMessage(chatId, membersInChat, messageResponse);
        messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/restore", messageResponse);
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
            if (TimeUnit.MILLISECONDS.toHours(now.getTime() - message.getDeletedTime().getTime()) >= 1) {
                if (!(message.getType().equals(MessageType.TEXT) || message.getType().equals(MessageType.TEXT_FORWARDED))) {
                    s3Service.deleteFile(message.getContent());
                }
                removeMessage(message.getMessageId());
                System.out.println("Removed unsent message: " + message.getMessageId());
            }
        }
    }

}
