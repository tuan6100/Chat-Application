package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;


    private Account getAuthenticatedAccount() throws ChatException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ChatException("User is not authenticated");
        }
        String email = auth.getName();
        return accountService.findAccount(email);
    }

    @GetMapping("/info")
    public Authentication getInfo() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/me")
    public ResponseEntity<Account> getCurrentAccountInfo() throws ChatException {
        Account account = getAuthenticatedAccount();
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccountInfo(@PathVariable Long accountId) throws ChatException {
        Account currentAccount = getAuthenticatedAccount();
        if (!currentAccount.getAccountId().equals(accountId)) {
            throw new ChatException("You do not have permission to view this account");
        }
        Account account = accountService.findAccount(accountId);
        return ResponseEntity.ok(account);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/update")
    public ResponseEntity<Account> updateAccount(@RequestBody AccountDTO accountDTO) throws ChatException {
        Account account = getAuthenticatedAccount();
        accountService.updateAccount(account.getAccountId(), accountDTO);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/me/reset-password")
    public ResponseEntity<Account> resetPassword(@RequestBody Map<String, String> requestPassword) throws ChatException {
        Account account = getAuthenticatedAccount();
        String oldPassword = requestPassword.get("oldPassword");
        String newPassword = requestPassword.get("newPassword");
        accountService.resetPassword(account.getAccountId(), oldPassword, newPassword);
        return ResponseEntity.ok(account);
    }
}


