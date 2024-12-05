package com.chat.app.controller;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.dto.FriendStatusDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.service.AccountService;
import com.chat.app.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RelationshipService relationshipService;

    Account getAuthenticatedAccount() throws ChatException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ChatException("User is not authenticated");
        }
        String email = auth.getName();
        return accountService.getAccount(email);
    }

    @GetMapping("/info")
    public Authentication getInfo() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getCurrentAccountInfo() throws ChatException {
        Account account = getAuthenticatedAccount();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<AccountResponse.FriendResponse> friends = relationshipService.getFriendsList(account.getAccountId());
        AccountResponse response = new AccountResponse(
                account.getAccountId(),
                account.getUsername(),
                account.getAvatar(),
                friends
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountInfo(@PathVariable Long accountId) throws ChatException {
        Account currentAccount = getAuthenticatedAccount();
        if (!currentAccount.getAccountId().equals(accountId)) {
            throw new ChatException("You do not have permission to view this account");
        }
        Account account = accountService.getAccount(accountId);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/avatar")
    public ResponseEntity<String> getCurrentAccountAvatar() throws ChatException {
        Account account = getAuthenticatedAccount();
        return ResponseEntity.ok(account.getAvatar());
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/update")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountDTO accountDTO) throws ChatException {
        Account account = getAuthenticatedAccount();
        accountService.updateAccount(account.getAccountId(), accountDTO);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/invite")
    public ResponseEntity<Relationship> inviteFriend(@RequestParam Long friendId) throws ChatException {
        Account user = getAuthenticatedAccount();
        Account friend = accountService.getAccount(friendId);
        relationshipService.inviteFriend(user.getAccountId(), friendId);
        return ResponseEntity.ok(new Relationship(user, friend, RelationshipStatus.WAITING_TO_ACCEPT, new Date()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/accept")
    public ResponseEntity<Relationship> acceptFriend(@RequestParam Long friendId) throws ChatException {
        Account user = getAuthenticatedAccount();
        Account friend = accountService.getAccount(friendId);
        relationshipService.acceptFriend(user.getAccountId(), friendId);
        return ResponseEntity.ok(new Relationship(friend, user, RelationshipStatus.ACCEPTED, new Date()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/refuse")
    public ResponseEntity<String> refuseFriend(@RequestParam Long friendId) throws ChatException {
        Account user = getAuthenticatedAccount();
        Account friend = accountService.getAccount(friendId);
        relationshipService.refuseFriend(user.getAccountId(), friendId);
        return ResponseEntity.ok("Refused a friend request from " + friend.getUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me/invitations")
    public ResponseEntity<List<FriendStatusDTO>> getInvitationsList() throws ChatException {
        Account user = getAuthenticatedAccount();
        return ResponseEntity.ok(relationshipService.getInvitationsList(user.getAccountId()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/unfriend")
    public ResponseEntity<Void> unfriend(@RequestParam Long friendId) throws ChatException {
        Account user = getAuthenticatedAccount();
        relationshipService.unfriend(user.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/block")
    public ResponseEntity<Void> block(@RequestParam Long friendId) throws ChatException {
        Account user = getAuthenticatedAccount();
        relationshipService.blockFriend(user.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/me/unblock")
    public ResponseEntity<Void> unblock(@RequestParam Long friendId) throws ChatException {
        Account user = getAuthenticatedAccount();
        relationshipService.unblockFriend(user.getAccountId(), friendId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete() throws ChatException {
        Account user = getAuthenticatedAccount();
        accountService.deleteAccount(user.getAccountId());
        return ResponseEntity.ok("Account deleted successfully");
    }
}
