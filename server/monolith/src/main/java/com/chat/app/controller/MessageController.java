package com.chat.app.controller;

import com.chat.app.payload.request.*;
import com.chat.app.service.interfaces.message.kafka.MessageProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class MessageController {

    @Autowired
    private MessageProducerService messageProducerService;



    @MessageMapping("/chat/{chatId}/message/send")
    public void sendMessage(@DestinationVariable Long chatId, @Payload NewMessageRequest request) {
        if (request == null || request.equals(new MessageRequest())) {
            return;
        }
        request.setSentTime(new Date(request.getSentTime().getTime()));
        messageProducerService.produceMessageSending(chatId, request);
    }

//    @MessageMapping("/chat/{chatId}/message/call")
//    public void sendCallMessage(@DestinationVariable Long chatId, @Payload MessageCallRequest request) throws ChatException {
//        if (request == null || request.equals(new MessageCallRequest())) {
//            return;
//        }
//        messageProducerService.sendCallMessage(chatId, request);
//    }

    @MessageMapping("/chat/{chatId}/message/mark-seen")
    public void markViewedMessage(@DestinationVariable Long chatId, @Payload MessageSeenRequest request) {
        if (request.getMessageId() == null) {
            return;
        }
       messageProducerService.produceMessageMarkedAsViewing(chatId, request);
    }

    @MessageMapping("/chat/{chatId}/message/update")
    public void editMessage(@DestinationVariable Long chatId, @Payload MessageUpdateRequest request) {
        if (request == null || request.equals(new MessageUpdateRequest())) {
            return;
        }
        messageProducerService.produceMessageUpdating(chatId, request);
    }

    @MessageMapping("/chat/{chatId}/message/delete")
    public void deleteMessage(@DestinationVariable Long chatId, @Payload MessageRequest request) {
        messageProducerService.produceMessageDeleting(chatId, request);
    }

    @MessageMapping("/chat/{chatId}/message/restore")
    public void restoreMessage(@DestinationVariable Long chatId, @Payload MessageRequest request) {
        messageProducerService.produceMessageRestoring(chatId,request);
    }
}