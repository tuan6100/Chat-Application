package com.chat.app.controller;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.payload.request.MessageRequest;
import com.chat.app.service.ChatService;
import com.chat.app.service.MessageService;
import com.chat.app.service.PrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private PrivateChatService privateChatService;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("{chatId}")
    public ResponseEntity<? extends Chat> getChat(@PathVariable Long chatId) throws ChatException {
        Chat chat = chatService.findChat(chatId);
        if (chat instanceof PrivateChat privateChat) {
            return ResponseEntity.ok(privateChat);
        } else if (chat instanceof GroupChat groupChat) {
            return ResponseEntity.ok(groupChat);
        } else {
            throw new ChatException("Unknown chat type");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("{chatId}")
    @MessageMapping("{chatId}/message")
    @SendTo("/client/{chatId}")
    public ResponseEntity<Message> sendMessage(@PathVariable Long chatId, @RequestBody MessageRequest request) throws ChatException {
        Message message = messageService.sendMessage(chatId, request);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("{chatId}")
    public ResponseEntity<Chat> changeTheme(@PathVariable Long chatId, @RequestParam String theme) throws ChatException {
        Theme themeEnum = Theme.valueOf(theme.toUpperCase());
        Chat chat = chatService.changeTheme(chatId, themeEnum);
        return ResponseEntity.ok(chat);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("{chatId}/message/reply")
    public ResponseEntity<Message> replyMessage(@PathVariable Long chatId,
                                                @RequestParam Long repliedMessageId,
                                                @RequestBody MessageRequest request
                                                ) throws ChatException {
        Message message = messageService.replyMessage(chatId, repliedMessageId, request);
        chatService.addMessage(chatId, message.getMessageId());
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("{chatId}/message")
    public ResponseEntity<String> removeMessage(@PathVariable Long chatId, @RequestParam Long messageId) throws ChatException {
        chatService.removeMessage(chatId, messageId);
        return ResponseEntity.ok("Message removed");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("{chatId}/message/viewed")
    public ResponseEntity<Message> viewMessage(@PathVariable Long chatId, @RequestBody Map<Long, Long> viewerMap) throws ChatException {
        if (!(viewerMap.containsKey("messageId")) || (!viewerMap.containsKey("userId"))) {
            throw new ChatException("Invalid viewer map");
        }
        Long messageId = viewerMap.get("messageId");
        Long viewerId = viewerMap.get("viewerId");
        Message message = messageService.viewMessage(chatId, messageId, viewerId);
        return ResponseEntity.ok(message);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("{chatId}/message")
    public ResponseEntity<Message> editMessage(@PathVariable Long chatId,
                                               @RequestParam Long messageId,
                                               @RequestBody MessageRequest request
                                               ) throws ChatException {
        Message message = messageService.editMessage(chatId, messageId, request);
        return ResponseEntity.ok(message);
    }
}