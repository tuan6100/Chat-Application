package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    @Autowired
    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public/{chatId}")
    public Message sendMessage(@PathVariable Long chatId, @RequestBody MessageRequest messageRequest) throws ChatException {
        return messageService.sendTextMessage(chatId, messageRequest);
    }
}