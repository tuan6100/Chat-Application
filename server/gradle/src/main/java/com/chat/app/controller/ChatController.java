package com.chat.app.controller;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.exception.UnauthorizedException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.payload.request.MessageVerifierRequest;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.service.*;
import com.chat.app.service.redis.MessageCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chat/{chatId}")
public class ChatController {


    @Autowired
    private ChatService chatService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageCacheService messageCacheService;


    private Long getAuthenticatedAccountId() throws UnauthorizedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }
        String email = auth.getName();
        return accountService.getAccount(email).getAccountId();
    }

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
    @GetMapping("/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long chatId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @Value("${spring.redis.cache.messages-per-chat.size}") int size) {
        return ResponseEntity.ok(chatService.getMessages(chatId, getAuthenticatedAccountId(), page, size));
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/theme")
    public ResponseEntity<Chat> changeTheme(@PathVariable Long chatId, @RequestParam String theme) throws ChatException {
        Theme themeEnum = Theme.valueOf(theme.toUpperCase());
        Chat chat = chatService.changeTheme(chatId, themeEnum);
        return ResponseEntity.ok(chat);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/message/verify")
    public ResponseEntity<?> verifyMessage(@PathVariable Long chatId, @RequestBody MessageVerifierRequest request) throws ChatException {
        chatService.verifyMessage(chatId, getAuthenticatedAccountId(), request);
        return ResponseEntity.ok().build();
    }


}