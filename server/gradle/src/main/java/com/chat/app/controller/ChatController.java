package com.chat.app.controller;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.Message;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.service.ChatService;
import com.chat.app.service.GroupChatService;
import com.chat.app.service.PrivateChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/chat/{chatId}")
public class ChatController {


    @Autowired
    private ChatService chatService;

    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private GroupChatService groupChatService;


    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<? extends Chat> getChat(@PathVariable Long chatId) throws ChatException {
        Chat chat = chatService.getChat(chatId);
        if (chat instanceof PrivateChat privateChat) {
            return ResponseEntity.ok(privateChat);
        } else if (chat instanceof GroupChat groupChat) {
            return ResponseEntity.ok(groupChat);
        } else {
            throw new ChatException("Unknown chat type");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/theme")
    public ResponseEntity<Chat> changeTheme(@PathVariable Long chatId, @RequestParam String theme) throws ChatException {
        Theme themeEnum = Theme.valueOf(theme.toUpperCase());
        Chat chat = chatService.changeTheme(chatId, themeEnum);
        return ResponseEntity.ok(chat);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/messages")
    public Page<Message> getMessages(@PathVariable Long chatId, @RequestParam int page, @RequestParam int size) throws ChatException {
        return  chatService.getMessages(chatId, PageRequest.of(page, size));
    }

}