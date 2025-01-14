package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.payload.request.MessageSeenRequest;
import com.chat.app.payload.request.MessageVerifierRequest;
import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;


    @MessageMapping("/{chatId}/message/send")
    public void sendMessage(@DestinationVariable Long chatId, @Payload MessageRequest request) {
        request.setSentTime(new Date(request.getSentTime().getTime()));
        messageService.sendMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/mark-seen")
    public void markViewedMessage(@DestinationVariable Long chatId, @Payload MessageSeenRequest request) throws ChatException {
       messageService.markViewedMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/edit")
    public void editMessage(@RequestParam Long messageId, MessageRequest request) throws ChatException {
       messageService.editMessage(messageId, request);
    }

    @MessageMapping("/{chatId}/message/unsend")
    public void unsendMessage(@RequestParam Long messageId) {
        messageService.unsendMessage(messageId);
    }
}