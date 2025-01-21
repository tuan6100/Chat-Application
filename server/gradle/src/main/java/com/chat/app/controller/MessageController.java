package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.payload.request.MessageSeenRequest;
import com.chat.app.payload.request.MessageUpdateRequest;
import com.chat.app.service.MessageService;
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
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/{chatId}/message/send")
    public void sendMessage(@DestinationVariable Long chatId, @Payload MessageRequest request) {
        if (request == null || request.equals(new MessageRequest())) {
            return;
        }
        request.setSentTime(new Date(request.getSentTime().getTime()));
        messageService.sendMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/mark-seen")
    public void markViewedMessage(@DestinationVariable Long chatId, @Payload MessageSeenRequest request) throws ChatException {
        if (request.getMessageId() == null) {
            return;
        }
       messageService.markViewedMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/update")
    public void editMessage(@DestinationVariable Long chatId, @Payload MessageUpdateRequest request) throws ChatException {
        if (request == null || request.equals(new MessageUpdateRequest())) {
            return;
        }
        messageService.updateMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/delete")
    public void deleteMessage(@DestinationVariable Long chatId, @Payload Long messageId) throws ChatException {
        messageService.unsendMessage(chatId, messageId);
    }

    @MessageMapping("/{chatId}/message/restore")
    public void restoreMessage(@DestinationVariable Long chatId, @Payload Long messageId) throws ChatException {
        messageService.restoreMessage(chatId, messageId);
    }

    @MessageMapping("/ping")
    public void ping(@Payload String ping) {
        messagingTemplate.convertAndSend("/client/pong", "Pong");
    }
}