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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/{chatId}/message")
    @SendTo("/client/{chatId}")
    public Message sendMessage(@DestinationVariable Long chatId, MessageRequest request) {
        return messageService.sendMessage(chatId, request);
    }

    @MessageMapping("/{chatId}/message/mark-viewed")
    @SendTo("/client/{chatId}")
    public Message markViewedMessage(@RequestParam Long messageId, @RequestParam Long viewerId) throws ChatException {
        return messageService.markViewedMessage(messageId, viewerId);
    }

    @MessageMapping("/{chatId}/message/reply")
    @SendTo("/client/{chatId}")
    public Message replyMessage(@DestinationVariable Long chatId,
                                @RequestParam Long repliedMessageId,
                                MessageRequest request) {
        return messageService.replyMessage(chatId, repliedMessageId, request);
    }

    @MessageMapping("/{chatId}/message/edit")
    @SendTo("/client/{chatId}")
    public Message editMessage(@RequestParam Long messageId, MessageRequest request) {
        return messageService.editMessage(messageId, request);
    }

    @MessageMapping("/{chatId}/message/unsend")
    @SendTo("/client/{chatId}")
    public void unsendMessage(@RequestParam Long messageId) {
        messageService.unsendMessage(messageId);
    }
}