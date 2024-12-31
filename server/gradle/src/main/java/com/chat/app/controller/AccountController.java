package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.exception.UnauthorizedException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.dto.FriendStatusDTO;
import com.chat.app.model.elasticsearch.AccountIndex;
import com.chat.app.model.entity.Account;
import com.chat.app.model.redis.AccountOnlineStatus;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.payload.response.NotificationResponse;
import com.chat.app.payload.response.RelationshipResponse;
import com.chat.app.service.AccountService;
import com.chat.app.service.NotificationService;
import com.chat.app.service.RelationshipService;
import com.chat.app.service.elasticsearch.AccountSearchService;
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
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    private Account getAuthenticatedAccount() throws UnauthorizedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }
        String email = auth.getName();
        return accountService.getAccount(email);
    }

    @GetMapping("/info")
    public Authentication getInfo() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<AccountResponse>> searchAccounts(@RequestParam String username) {
        List<AccountIndex> results = accountSearchService.searchAccount(username);
        List<AccountResponse> responses = new ArrayList<>();
        results.forEach(result -> responses.add(new AccountResponse(result.getAccountId(), result.getUsername(), result.getAvatar())));
        responses.forEach(account -> account.setIsOnline(accountService.isUserOnline(account.getAccountId())));
        responses.forEach(account -> account.setLastOnlineTime(accountService.getLastOnlineTime(account.getAccountId())));
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountInfo(@PathVariable Long accountId) throws ChatException, UnauthorizedException {
        return ResponseEntity.ok(accountService.getAccountResponse(accountId));
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
        Account user = getAuthenticatedAccount();
        List<AccountResponse> friends = relationshipService.getFriendsList(user.getAccountId());
        friends.forEach(friend -> friend.setIsOnline(accountService.isUserOnline(friend.getAccountId())));
        friends.forEach(friend -> friend.setLastOnlineTime(accountService.getLastOnlineTime(friend.getAccountId())));
        return ResponseEntity.ok(friends);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/update")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountDTO accountDTO) throws ChatException, UnauthorizedException {
        Account account = getAuthenticatedAccount();
        accountService.updateAccount(account.getAccountId(), accountDTO);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/relationship")
    public ResponseEntity<RelationshipResponse> getRelationship(@RequestParam Long accountId) throws ChatException, UnauthorizedException {
        Account user = getAuthenticatedAccount();
        return ResponseEntity.ok(relationshipService.getRelationshipStatus(user.getAccountId(), accountId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/invite")
    public ResponseEntity<RelationshipResponse> inviteFriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account user = getAuthenticatedAccount();
        Account friend = accountService.getAccount(friendId);
        relationshipService.inviteFriend(user.getAccountId(), friendId);
        notificationService.notifyFriendRequestInvited(user.getAccountId(), friendId);
        return ResponseEntity.ok(relationshipService.getRelationshipStatus(user.getAccountId(), friendId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/accept")
    public ResponseEntity<RelationshipResponse> acceptFriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account user = getAuthenticatedAccount();
        Account friend = accountService.getAccount(friendId);
        relationshipService.acceptFriend(user.getAccountId(), friendId);
        notificationService.notifyFriendRequestAccepted(user.getAccountId(), friendId);
        return ResponseEntity.ok(relationshipService.getRelationshipStatus(user.getAccountId(), friendId));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/reject")
    public ResponseEntity<String> rejectFriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account user = getAuthenticatedAccount();
        Account friend = accountService.getAccount(friendId);
        relationshipService.rejectFriend(user.getAccountId(), friendId);
        notificationService.notifyFriendRequestRejected(friendId, user.getAccountId());
        return ResponseEntity.ok("Reject a friend request from " + friend.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/invitations")
    public ResponseEntity<List<FriendStatusDTO>> getInvitationsList() throws UnauthorizedException {
        Account user = getAuthenticatedAccount();
        return ResponseEntity.ok(relationshipService.getInvitationsList(user.getAccountId()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/unfriend")
    public ResponseEntity<Void> unfriend(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account user = getAuthenticatedAccount();
        relationshipService.unfriend(user.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/block")
    public ResponseEntity<Void> block(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account user = getAuthenticatedAccount();
        relationshipService.blockUser(user.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/unblock")
    public ResponseEntity<Void> unblock(@RequestParam Long friendId) throws ChatException, UnauthorizedException {
        Account user = getAuthenticatedAccount();
        relationshipService.unblockUser(user.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete() throws UnauthorizedException {
        Account user = getAuthenticatedAccount();
        accountService.deleteAccount(user.getAccountId());
        return ResponseEntity.ok("Account deleted successfully");
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
        Account user = getAuthenticatedAccount();
        boolean isOnline = accountService.isUserOnline(user.getAccountId());
        Date lastOnline = accountService.getLastOnlineTime(user.getAccountId());
        Map<String, Object> response = Map.of(
                "isOnline", isOnline,
                "lastOnlineTime", lastOnline
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications() throws UnauthorizedException {
        Account user = getAuthenticatedAccount();
        return ResponseEntity.ok(notificationService.getUserNotifications(user.getAccountId()));
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
