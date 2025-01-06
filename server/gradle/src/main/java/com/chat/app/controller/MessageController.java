package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/{chatId}/message/send")
    public void sendMessage(@DestinationVariable Long chatId, MessageRequest request) {
        messagingTemplate.convertAndSend("/client/chat/" + chatId, MessageResponse.fromRequest(request));
        messageService.sendMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/mark-viewed")
    public void markViewedMessage(@RequestParam Long messageId, @RequestParam Long viewerId) throws ChatException {
       messageService.markViewedMessage(messageId, viewerId);
    }

    @MessageMapping("/{chatId}/message/reply")
    public void replyMessage(@DestinationVariable Long chatId,
                                @RequestParam Long repliedMessageId,
                                MessageRequest request) {
       messageService.replyMessage(chatId, repliedMessageId, request);
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