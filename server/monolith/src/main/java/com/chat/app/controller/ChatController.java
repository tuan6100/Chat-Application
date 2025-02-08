package com.chat.app.controller;

import com.chat.app.enumeration.Theme;
import com.chat.app.exception.ChatException;
import com.chat.app.exception.UnauthorizedException;
import com.chat.app.model.entity.Chat;
import com.chat.app.model.entity.extend.chat.GroupChat;
import com.chat.app.model.entity.extend.chat.PrivateChat;
import com.chat.app.payload.request.MessageConfirmationRequest;
import com.chat.app.payload.response.MessageResponse;
import com.chat.app.service.interfaces.chat.ChatService;
import com.chat.app.service.interfaces.chat.GroupChatService;
import com.chat.app.service.interfaces.chat.PrivateChatService;

import com.chat.app.service.interfaces.message.MessageSearchService;
import com.chat.app.service.interfaces.message.kafka.MessageProducerService;
import com.chat.app.service.implementations.message.MessageRedisCacheServiceImpl;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("chatServiceImpl")
    private ChatService chatService;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private MessageProducerService messageProducerService;

    @Autowired
    private MessageRedisCacheServiceImpl messageRedisCacheServiceImpl;

    @Autowired
    private MessageSearchService messageSearchService;


    private Long getAuthenticatedAccountId() throws UnauthorizedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }
        String email = auth.getName();
        return accountSearchService.searchAccountByEmail(email).getAccountId();
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
                                                             @RequestParam int size) {
        List<MessageResponse> responses = chatService.getMessagesByPage(chatId, page, size);
        if (page >= chatService.getMaxPageOfMessages(chatId, size) ) {
            return null;
        }
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/theme")
    public ResponseEntity<Chat> changeTheme(@PathVariable Long chatId, @RequestParam String theme) {
        Theme themeEnum = Theme.valueOf(theme.toUpperCase());
        Chat chat = chatService.changeTheme(chatId, themeEnum);
        return ResponseEntity.ok(chat);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/message/verify")
    public ResponseEntity<?> verifyMessage(@PathVariable Long chatId,
                                           @RequestBody MessageConfirmationRequest request
    ) {
        if (!request.getSenderId().equals(getAuthenticatedAccountId())) {
            return null;
        }
        messageProducerService.produceMessageConfirmation(chatId, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/messages/search")
    public ResponseEntity<List<Long>> searchMessage(@PathVariable Long chatId,
                                                    @RequestParam String content
    ) {
        return ResponseEntity.ok(messageSearchService.searchMessagesByKeyword(chatId, content));
    }


}