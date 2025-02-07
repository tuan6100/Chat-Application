package com.chat.app.controller;


import com.chat.app.exception.ChatException;
import com.chat.app.exception.UnauthorizedException;
import com.chat.app.payload.response.FriendStatusResponse;
import com.chat.app.model.elasticsearch.AccountIndex;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.SpamChat;
import com.chat.app.model.redis.AccountOnlineStatus;
import com.chat.app.payload.request.AccountRequest;
import com.chat.app.payload.response.*;
import com.chat.app.service.interfaces.chat.*;
import com.chat.app.service.interfaces.system.notification.FriendRequestNotificationService;
import com.chat.app.service.interfaces.system.notification.NotificationService;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.user.information.AccountService;
import com.chat.app.service.interfaces.user.relationship.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private FriendRequestNotificationService friendRequestNotificationService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private PrivateChatService privateChatService;

    @Autowired
    private GroupChatService groupChatService;

    @Autowired
    private CloudStorageService cloudStorageService;

    @Autowired
    private SpamChatService spamChatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;


    private Account getAuthenticatedAccount() throws UnauthorizedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }
        String email = auth.getName();
        return accountSearchService.searchAccountByEmail(email);
    }


    @GetMapping("/{accountId}/info")
    public ResponseEntity<AccountResponse> getAccountInfo(@PathVariable Long accountId) throws ChatException, UnauthorizedException {
        return ResponseEntity.ok(accountService.getAccountResponse(accountId));
    }

    @GetMapping("/{accountId}/profile")
    public ResponseEntity<Account> getAccountProfile(@PathVariable Long accountId) throws ChatException {
        return ResponseEntity.ok(accountSearchService.searchAccountById(accountId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AccountResponse>> searchAccounts(@RequestParam String username) {
        List<AccountIndex> results = accountSearchService.searchAccountByUsername(username);
        List<AccountResponse> responses = results.stream().parallel()
                .map(result -> new AccountResponse(result.getAccountId(), result.getUsername(), result.getAvatar()))
                .peek(account -> account.setIsOnline(accountService.isUserOnline(account.getAccountId())))
                .peek(account -> account.setLastOnlineTime(accountService.getLastOnlineTime(account.getAccountId())))
                .peek(account -> account.setRelationshipStatus(relationshipService.getRelationshipStatus(getAuthenticatedAccount().getAccountId(), account.getAccountId()).getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getCurrentAccountInfo() throws UnauthorizedException {
        Account account = getAuthenticatedAccount();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        AccountResponse response = new AccountResponse(
                account.getAccountId(),
                account.getUsername(),
                account.getEmail(),
                account.getAvatar()
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/avatar")
    public ResponseEntity<String> getCurrentAccountAvatar() throws UnauthorizedException {
        Account account = getAuthenticatedAccount();
        return ResponseEntity.ok(account.getAvatar());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/friends")
    public ResponseEntity<List<AccountResponse>> getFriendsList() throws UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        List<AccountResponse> friends = relationshipService.getFriendsList(myAccount.getAccountId());
//        friends.forEach(friend -> friend.setIsOnline(accountService.isUserOnline(friend.getAccountId())));
//        friends.forEach(friend -> friend.setLastOnlineTime(accountService.getLastOnlineTime(friend.getAccountId())));
        friends = friends.stream().parallel()
                .peek(friend -> friend.setIsOnline(accountService.isUserOnline(friend.getAccountId())))
                .peek(friend -> friend.setLastOnlineTime(accountService.getLastOnlineTime(friend.getAccountId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(friends);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/update")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountRequest accountRequest) throws ChatException, UnauthorizedException {
        Account account = getAuthenticatedAccount();
        accountService.updateAccount(account.getAccountId(), accountRequest);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/relationship")
    public ResponseEntity<RelationshipResponse> getRelationship(@RequestParam Long accountId) throws ChatException, UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        return ResponseEntity.ok(relationshipService.getRelationshipStatus(myAccount.getAccountId(), accountId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/invite")
    public ResponseEntity<RelationshipResponse> inviteFriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        relationshipService.inviteFriend(myAccount.getAccountId(), friendId);
        friendRequestNotificationService.notifyFriendRequestInvited(myAccount.getAccountId(), friendId);
        return ResponseEntity.ok(relationshipService.getRelationshipStatus(myAccount.getAccountId(), friendId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/accept")
    public ResponseEntity<RelationshipResponse> acceptFriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        relationshipService.acceptFriend(myAccount.getAccountId(), friendId);
        friendRequestNotificationService.notifyFriendRequestAccepted(myAccount.getAccountId(), friendId);
        return ResponseEntity.ok(relationshipService.getRelationshipStatus(myAccount.getAccountId(), friendId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/reject")
    public ResponseEntity<RelationshipResponse> rejectFriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();

        relationshipService.rejectFriend(myAccount.getAccountId(), friendId);
        return ResponseEntity.ok(relationshipService.getRelationshipStatus(myAccount.getAccountId(), friendId));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/invitations")
    public ResponseEntity<List<FriendStatusResponse>> getInvitationsList() throws UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        return ResponseEntity.ok(relationshipService.getInvitationsList(myAccount.getAccountId()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/unfriend")
    public ResponseEntity<Void> unfriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        relationshipService.unfriend(myAccount.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/block")
    public ResponseEntity<Void> block(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        relationshipService.blockUser(myAccount.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/unblock")
    public ResponseEntity<Void> unblock(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        relationshipService.unblockUser(myAccount.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/chats")
    public ResponseEntity<List<ChatResponse>> getChatIdsList() throws UnauthorizedException, ChatException {
        Account myAccount = getAuthenticatedAccount();
        return ResponseEntity.ok(chatService.getAllChatsByAccountId(myAccount.getAccountId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/chats/private-chat")
    public ResponseEntity<List<ChatResponse>> getChatList() throws UnauthorizedException, ChatException {
        Account myAccount = getAuthenticatedAccount();
        return ResponseEntity.ok(privateChatService.getAllPrivateChatsByAccountId(myAccount.getAccountId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/chats/group-chat")
    public ResponseEntity<List<ChatResponse>> getGroupChatList() throws UnauthorizedException, ChatException {
        Account myAccount = getAuthenticatedAccount();
        return ResponseEntity.ok(groupChatService.getAllGroupChatsByMemberId(myAccount.getAccountId()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/chats/cloud-storage")
    public ResponseEntity<ChatResponse> getCloudStorage() throws UnauthorizedException, ChatException {
        Account myAccount = getAuthenticatedAccount();
        return ResponseEntity.ok(cloudStorageService.getCloudStorageByAccountId(myAccount.getAccountId()));
    }

    @PostMapping("/me/online")
    public ResponseEntity<?> markOnline(@RequestParam Long accountId) throws  UnauthorizedException {
        accountService.markUserOnline(accountId);
        broadcastOnlineStatus(accountId, true);
        return ResponseEntity.ok(Map.of("message", "online"));
    }


    @PostMapping("/me/offline")
    public ResponseEntity<?> markOffline(@RequestParam Long accountId) throws UnauthorizedException {
        accountService.markUserOffline(accountId);
        broadcastOnlineStatus(accountId, false);
        return ResponseEntity.ok(Map.of("message", "offline"));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/status")
    public ResponseEntity<?> checkStatus() throws UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        boolean isOnline = accountService.isUserOnline(myAccount.getAccountId());
        Date lastOnline = accountService.getLastOnlineTime(myAccount.getAccountId());
        Map<String, Object> response = Map.of(
                "isOnline", isOnline,
                "lastOnlineTime", lastOnline
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications() throws UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        return ResponseEntity.ok(notificationService.getNotificationsByAccountId(myAccount.getAccountId()));
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/spam-chat")
    public ResponseEntity<List<MessageResponse>> getSpamChat(@RequestParam Long userId) throws UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        SpamChat spamChat = spamChatService.getSpamChat(myAccount.getAccountId(), userId);
        List<MessageResponse> messages = new ArrayList<>();
        spamChat.getMessages().forEach(message -> messages.add(MessageResponse.fromEntity(message)));
        return ResponseEntity.ok(messages);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete() throws UnauthorizedException {
        Account myAccount = getAuthenticatedAccount();
        accountService.deleteAccount(myAccount.getAccountId());
        return ResponseEntity.ok("Account deleted successfully");
    }

    @MessageMapping("/status")
    public void broadcastOnlineStatus(Long accountId, boolean isOnline) {
        AccountOnlineStatus status = new AccountOnlineStatus();
        status.setAccountId(accountId.toString());
        status.setIsOnline(isOnline);
        status.setLastOnlineTime(new Date());
        messagingTemplate.convertAndSend("/client/online-status", status);
    }
}
