package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.repository.AccountRepository;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    private TokenProvider tokenProvider;

    @Override
    public Account findAccount(Long accountId) throws ChatException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ChatException("Account not found"));
    }

    @Override
    public Account findAccountProfile(String jwt) {
        String username = tokenProvider.getUsernameFromToken(jwt);
        if (username == null) {
            throw new BadCredentialsException("Invalid token");
        }
        Account account = accountRepository.findByEmail(username);
        if (account == null) {
            throw new BadCredentialsException("Account not found");
        }
        return account;
    }

    @Override
    public Account createAccount(Account account) throws ChatException {
        return accountRepository.save(account);
    }

    @Override
    public Account updateAccount(Long accountId, AccountDTO accountDto) throws ChatException {
        Account account = findAccount(accountId);
        if (account == null) {
            throw new ChatException("Account not found");
        }
        if (accountDto.getUsername() != null) {
            account.setUsername(accountDto.getUsername());
        }
        if (accountDto.getAvatarImagePath() != null) {
            account.setAvatar(accountDto.getAvatarImagePath());
        }
        if (accountDto.getBirthDate() != null) {
            account.setBirthDate(accountDto.getBirthDate());
        }
        if (accountDto.getGender() != null) {
            account.setGender(accountDto.getGender());
        }
        if (accountDto.getBio() != null) {
            account.setBio(accountDto.getBio());
        }
        return accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    @Override
    public List<Account> searchAccount(String query) {
        return List.of();
    }

}
