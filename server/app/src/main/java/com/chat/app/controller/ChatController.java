package com.chat.app.controller;

import com.chat.app.model.entity.Message;
import com.chat.app.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@Controller
public class ChatController {

    private final MessageService messageService;

    @Autowired
    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(Message message) {
        return messageService.saveMessage(message);
    }
}