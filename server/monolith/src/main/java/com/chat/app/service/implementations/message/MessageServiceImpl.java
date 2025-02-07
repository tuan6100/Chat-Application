package com.chat.app.service.implementations.message;

import com.chat.app.dto.ChatMessage;
import com.chat.app.enumeration.MessageType;
import com.chat.app.exception.ChatException;
import com.chat.app.model.elasticsearch.MessageIndex;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.MessageReaction;
import com.chat.app.payload.request.*;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.repository.elasticsearch.MessageSearchRepository;
import com.chat.app.repository.jpa.MessageRepository;
import com.chat.app.service.interfaces.message.MessageProcessingService;
import com.chat.app.service.interfaces.message.MessageSearchService;
import com.chat.app.service.interfaces.message.caching.MessageLocalCacheService;
import com.chat.app.service.interfaces.message.caching.MessageRedisCacheService;
import com.chat.app.service.interfaces.message.kafka.MessageConsumerService;
import com.chat.app.service.interfaces.system.aws.S3Service;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class MessageServiceImpl implements MessageSearchService, MessageProcessingService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageSearchRepository messageSearchRepository;

    @Autowired
    private MessageRedisCacheService messageRedisCacheService;

    @Autowired
    private MessageLocalCacheService messageLocalCacheService;

    @Autowired
    @Lazy
    private MessageConsumerService messageConsumerService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private AccountSearchService  accountSearchService;

    @Autowired
    private KafkaTemplate<String, ChatMessage> kafkaTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private S3Service s3Service;


    @Override
    public Message searchMessageById(Long messageId) throws ChatException {
        return messageRepository.findById(messageId).
                orElseThrow(() -> new ChatException("Message not found"));
    }

    @Override
    public List<MessageIndex> filterMessagesByKeyword(Long chatId, String keyword) {
        return messageSearchRepository.findByContent(keyword, chatId);
    }

    @Override
    public List<Long> searchMessagesByKeyword(Long chatId, String keyword) {
        List<MessageIndex> messageIndices = filterMessagesByKeyword(chatId, keyword);
        List<Long> result = new ArrayList<>();
        for (MessageIndex messageIndex : messageIndices) {
            result.add(messageIndex.getMessageId());
        }
        return result;
    }

    @Override
    @Transactional
    public void processMessageSent(ChatMessage chatMessage) {
        try {
            Long chatId = chatMessage.getChatId();
            NewMessageRequest newMessageRequest = (NewMessageRequest) chatMessage.getMessageRequest();
            Chat chat = chatService.getChat(chatId);
            Account sender = accountSearchService.searchAccountById(newMessageRequest.getAccountId());
            MessageType type = MessageType.valueOf(newMessageRequest.getType().toUpperCase());
            Date sentTime = newMessageRequest.getSentTime();
            Message message = new Message(newMessageRequest.getRandomId(), sender, newMessageRequest.getContent(), type, sentTime, chat);
            if (newMessageRequest.getReplyToMessageId() != null) {
                Message replyToMessage = searchMessageById(newMessageRequest.getReplyToMessageId());
                message.setReplyTo(replyToMessage);
            }
            message = messageRepository.save(message);
            messageSearchRepository.save(new MessageIndex(message.getMessageId(), chatId, message.getContent()));
            MessageResponse messageResponse = MessageResponse.fromEntity(message);
            messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/send", messageResponse);
            System.out.println("Sent message: " + newMessageRequest.getContent() + " to chat: " + chatId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e instanceof ChatException) {
                return;
            }
            chatMessage.incrementRetryCount();
            messageConsumerService.consumeMessageSent(chatMessage);
        }
    }

    @Override
    @Transactional
    public void processMessageConfirmation(ChatMessage chatMessage) {
        try {
            Long chatId = chatMessage.getChatId();
            MessageConfirmationRequest request = chatMessage.getMessageConfirmationRequest();
            Message message = messageRepository.findByRandomId(request.getRandomId());
            if (message == null) {
                return;
            }
            System.out.println("Verifying message: " + message.getContent());
            if (request.getStatus().equals("sent")) {
                message.setRandomId(null);
                message = messageRepository.save(message);
                MessageResponse messageResponse = MessageResponse.fromEntity(message);
                messageRedisCacheService.cacheNewMessage(chatId, messageResponse);
                messageLocalCacheService.putMessagesToTheBottom(chatId, List.of(messageResponse));
                messageSearchRepository.save(new MessageIndex(message.getMessageId(), chatId, message.getContent()));
            } else if (request.getStatus().equals("failed")) {
                messageRepository.delete(message);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            chatMessage.incrementRetryCount();
            messageConsumerService.consumeMessageSent(chatMessage);
        }
    }

    @Override
    @Transactional
    public void processMessageMarkedAsViewed(ChatMessage chatMessage) {
        try {
            Long chatId = chatMessage.getChatId();
            MessageSeenRequest messageSeenRequest = (MessageSeenRequest) chatMessage.getMessageRequest();
            Message message = searchMessageById(messageSeenRequest.getMessageId());
            Account viewer = accountSearchService.searchAccountById(messageSeenRequest.getAccountId());
            if (message.getViewers().contains(viewer)) {
                return;
            }
            if (message.getSender().getAccountId().equals(messageSeenRequest.getViewerId())) {
                return;
            }
            message.getViewers().add(viewer);
            messageRepository.save(message);
            System.out.println("Marked message: " + message.getMessageId() + " as viewed by: " + viewer.getAccountId());
            MessageResponse messageResponse = MessageResponse.fromEntity(message);
            messageRedisCacheService.updateMessageInCache(chatId, messageResponse);
            messageLocalCacheService.updateMessageInCache(chatId, messageResponse);
            messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/send", messageResponse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e instanceof ChatException) {
                return;
            }
            chatMessage.incrementRetryCount();
            messageConsumerService.consumerMessageMarkedAsViewed(chatMessage);
        }
    }

    @Override
    @Transactional
    public void processMessageUpdated(ChatMessage chatMessage) {
        try {
            Long chatId = chatMessage.getChatId();
            MessageUpdateRequest messageUpdateRequest = (MessageUpdateRequest) chatMessage.getMessageRequest();
            Message message = searchMessageById(messageUpdateRequest.getMessageId());
            MessageResponse messageResponse = new MessageResponse();
            if (messageUpdateRequest.getContent() != null) {
                message.setContent(messageUpdateRequest.getContent());
                messageSearchRepository.save(new MessageIndex(message.getMessageId(), chatId, message.getContent()));
                messageResponse = MessageResponse.fromEntity(message);
                messageResponse.setStatus("edited");
            }
            if (messageUpdateRequest.getReaction() != null) {
                System.out.println("Updating message: " + messageUpdateRequest.getMessageId() + " with reaction: " + messageUpdateRequest.getReaction());
                Account account = accountSearchService.searchAccountById(messageUpdateRequest.getAccountId());
                if (message.getReactions().stream().parallel().anyMatch(reaction -> reaction.getAccount().getAccountId().equals(messageUpdateRequest.getAccountId()))) {
                    if (message.getReactions().stream().anyMatch(reaction -> reaction.getReaction().equals(messageUpdateRequest.getReaction()))) {
                        message.getReactions().removeIf(reaction -> reaction.getAccount().getAccountId().equals(messageUpdateRequest.getAccountId()));
                    } else {
                        message.getReactions().removeIf(reaction -> reaction.getAccount().getAccountId().equals(messageUpdateRequest.getAccountId()));
                        MessageReaction newReaction = new MessageReaction(message, account, messageUpdateRequest.getReaction());
                        message.getReactions().add(newReaction);
                    }
                } else {
                    MessageReaction reaction = new MessageReaction(message, account, messageUpdateRequest.getReaction());
                    message.getReactions().add(reaction);
                }
                messageResponse = MessageResponse.fromEntity(message);
            }
            messageRepository.save(message);
            messageRedisCacheService.updateMessageInCache(chatId, messageResponse);
            messageLocalCacheService.updateMessageInCache(chatId, messageResponse);
            messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/update", messageResponse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e instanceof ChatException) {
                return;
            }
            chatMessage.incrementRetryCount();
            messageConsumerService.consumeMessageDeleted(chatMessage);
        }
    }

    @Override
    @Transactional
    public void processMessageDeleted(ChatMessage chatMessage) {
        try {
            Long chatId = chatMessage.getChatId();
            MessageRequest messageRequest = chatMessage.getMessageRequest();
            Message message = searchMessageById(messageRequest.getMessageId());
            message.setUnsent(true);
            message.setDeletedTime(new Date());
            message = messageRepository.save(message);
            chatService.deleteMessage(messageRequest.getMessageId());
            MessageResponse messageResponse = MessageResponse.fromEntity(message);
            messageResponse.setStatus("deleted");
            messageResponse.setContent("");
            messageResponse.setType("TEXT");
            messageResponse.setReactions(null);
            messageRedisCacheService.updateMessageInCache(chatId, messageResponse);
            messageLocalCacheService.updateMessageInCache(chatId, messageResponse);
            messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/delete", messageResponse);
            System.out.println("Deleted message: " + messageResponse.getMessageId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e instanceof ChatException) {
                return;
            }
            chatMessage.incrementRetryCount();
            messageConsumerService.consumeMessageDeleted(chatMessage);
        }
    }

    @Override
    @Transactional
    public void processMessageRestored(ChatMessage chatMessage) {
        try {
            Long chatId = chatMessage.getChatId();
            MessageRequest messageRequest = chatMessage.getMessageRequest();
            Message message = searchMessageById(messageRequest.getMessageId());
            message.setUnsent(false);
            message.setDeletedTime(null);
            messageRepository.save(message);
            messageSearchRepository.save(new MessageIndex(message.getMessageId(), chatId, message.getContent()));
            MessageResponse messageResponse = MessageResponse.fromEntity(message);
            messageRedisCacheService.updateMessageInCache(chatId, messageResponse);
            messageLocalCacheService.updateMessageInCache(chatId, messageResponse);
            messagingTemplate.convertAndSend("/client/chat/" + chatId + "/message/restore", messageResponse);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (e instanceof ChatException) {
                return;
            }
            chatMessage.incrementRetryCount();
            messageConsumerService.consumeMessageDeleted(chatMessage);
        }
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    protected void removeUnsentMessages() {
        Date now = new Date();
        try (Stream<Message> unsentMessages = messageRepository.findStreamByUnsentTrue()) {
            unsentMessages.forEach(message -> {
                if (TimeUnit.MILLISECONDS.toHours(now.getTime() - message.getDeletedTime().getTime()) >= 1) {
                    if (!(message.getType().equals(MessageType.TEXT) || message.getType().equals(MessageType.TEXT_FORWARDED))) {
                        s3Service.deleteFile(message.getContent());
                    }
                    messageRepository.delete(message);
                    System.out.println("Removed unsent message: " + message.getMessageId());
                }
            });
        }
    }

}


