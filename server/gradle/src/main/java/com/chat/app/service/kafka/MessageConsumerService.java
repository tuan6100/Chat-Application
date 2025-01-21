package com.chat.app.service.kafka;


import com.chat.app.exception.ChatException;
import com.chat.app.payload.request.ChatMessageRequest;
import com.chat.app.payload.request.MessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



@Service
public class
MessageConsumerService {

    @Autowired
    private MessageProcessingService messageProcessingService;


    @KafkaListener(topics = "send-message", groupId = "chat-group")
    public void consumeSendMessage(ChatMessageRequest chatMessage) {
        if (chatMessage.getRetryCount() > 2) {
            return;
        }
        System.out.println("Kafka message received: " + chatMessage);
        messageProcessingService.processSendMessage(chatMessage);
    }

    @KafkaListener(topics = "edit-message", groupId = "chat-group")
    public void consumeEditMessage(Long messageId, MessageRequest messageRequest) throws ChatException {
        messageProcessingService.processEditMessage(messageId, messageRequest);
    }

    @KafkaListener(topics = "unsend-message", groupId = "chat-group")
    public void consumeUnsendMessage(String messageId) throws ChatException {
        messageProcessingService.processUnsendMessage(String.valueOf(messageId));
    }

    @KafkaListener(topics = "restore-message", groupId = "chat-group")
    public void consumeRestoreMessage(String messageId) throws ChatException {
        messageProcessingService.processRestoreMessage(String.valueOf(messageId));
    }
}
