package com.chat.app.service.implementations.message.kafka;


import com.chat.app.dto.ChatMessage;
import com.chat.app.payload.request.*;
import com.chat.app.service.interfaces.message.MessageProcessingService;
import com.chat.app.service.interfaces.message.kafka.MessageConsumerService;
import com.chat.app.service.interfaces.message.kafka.MessageProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class MessageQueueServiceImpl implements MessageProducerService, MessageConsumerService {

    @Autowired
    private MessageProcessingService messageProcessingService;

    @Autowired
    private KafkaTemplate<String, ChatMessage> kafkaTemplate;


    @Override
    public void produceMessageSending(Long chatId, NewMessageRequest newMessageRequest) {
        ChatMessage chatMessage = new ChatMessage(chatId, newMessageRequest);
        kafkaTemplate.send("send-message", chatMessage);
    }


    @Override
    @KafkaListener(topics = "send-message", groupId = "chat-group")
    public void consumeMessageSent(ChatMessage chatMessage) {
        if (chatMessage.isRetryLimitExceeded()) {
            return;
        }
        if (chatMessage.getMessageRequest() instanceof NewMessageRequest) {
            System.out.println("Kafka consumer received: " + chatMessage);
            messageProcessingService.processMessageSent(chatMessage);
        }
    }

    @Override
    public void produceMessageConfirmation(Long chatId, MessageConfirmationRequest messageConfirmationRequest) {
        ChatMessage chatMessage = new ChatMessage(chatId, messageConfirmationRequest);
        kafkaTemplate.send("confirm-message", chatMessage);
    }

    @Override
    @KafkaListener(topics = "confirm-message", groupId = "chat-group")
    public void consumeMessageConfirmation(ChatMessage chatMessage) {
        if (chatMessage.isRetryLimitExceeded()) {
            return;
        }
        if (chatMessage.getMessageConfirmationRequest() != null) {
            System.out.println("Kafka consumer received: " + chatMessage);
            messageProcessingService.processMessageConfirmation(chatMessage);
        }
    }

    @Override
    public void produceMessageMarkedAsViewing(Long chatId, MessageSeenRequest messageSeenRequest)  {
        ChatMessage chatMessage = new ChatMessage(chatId, messageSeenRequest);
        kafkaTemplate.send("mark-viewed-message", chatMessage);
    }

    @Override
    @KafkaListener(topics = "mark-viewed-message", groupId = "chat-group")
    public void consumerMessageMarkedAsViewed(ChatMessage chatMessage) {
        if (chatMessage.getRetryCount().get() > 2) {
            return;
        }
        if (chatMessage.getMessageRequest() instanceof MessageSeenRequest) {
            System.out.println("Kafka consumer received: " + chatMessage);
            messageProcessingService.processMessageMarkedAsViewed(chatMessage);
        }
    }

    @Override
    public void produceMessageUpdating(Long chatId, MessageUpdateRequest messageUpdateRequest)  {
        ChatMessage chatMessage = new ChatMessage(chatId, messageUpdateRequest);
        kafkaTemplate.send("update-message", chatMessage);
    }

    @Override
    @KafkaListener(topics = "update-message", groupId = "chat-group")
    public void consumeMessageUpdated(ChatMessage chatMessage) {
        if (chatMessage.isRetryLimitExceeded()) {
            return;
        }
        if (chatMessage.getMessageRequest() instanceof MessageUpdateRequest) {
            System.out.println("Kafka consumer received: " + chatMessage);
            messageProcessingService.processMessageUpdated(chatMessage);
        }
    }

    @Override
    public void produceMessageDeleting(Long chatId, MessageRequest messageRequest) {
        ChatMessage chatMessage = new ChatMessage(chatId, messageRequest);
        kafkaTemplate.send("delete-message", chatMessage);
    }

    @Override
    @KafkaListener(topics = "delete-message", groupId = "chat-group")
    public void consumeMessageDeleted(ChatMessage chatMessage) {
        if (chatMessage.isRetryLimitExceeded()) {
            return;
        }
        System.out.println("Kafka consumer received: " + chatMessage);
        messageProcessingService.processMessageDeleted(chatMessage);
    }

    @Override
    public void produceMessageRestoring(Long chatId, MessageRequest messageReplyRequest) {
        ChatMessage chatMessage = new ChatMessage(chatId, messageReplyRequest);
        kafkaTemplate.send("restore-message", chatMessage);
    }

    @Override
    @KafkaListener(topics = "restore-message", groupId = "chat-group")
    public void consumeMessageRestored(ChatMessage chatMessage) {
        if (chatMessage.isRetryLimitExceeded()) {
            return;
        }
        System.out.println("Kafka consumer received: " + chatMessage);
        messageProcessingService.processMessageRestored(chatMessage);
    }

}
