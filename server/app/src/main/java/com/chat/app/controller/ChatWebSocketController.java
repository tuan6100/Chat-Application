package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Message;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatWebSocketController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/{chatId}/message")
    @SendTo("/client/{chatId}")
    public Message sendMessage(@DestinationVariable Long chatId, MessageRequest request) throws ChatException {
        return messageService.sendMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/reply")
    @SendTo("/client/{chatId}")
    public Message replyMessage(@DestinationVariable Long chatId,
                                @RequestParam Long repliedMessageId,
                                MessageRequest request) throws ChatException {
        return messageService.replyMessage(chatId, repliedMessageId, request);
    }
}
