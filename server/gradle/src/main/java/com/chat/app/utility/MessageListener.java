package com.chat.app.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @KafkaListener(topics = "chat-group")
    public void listen(Long chatId, String message) {
        simpMessagingTemplate.convertAndSend("/client/" + chatId, message);
    }
}
