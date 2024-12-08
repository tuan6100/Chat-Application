package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.repository.AccountRepository;
import com.chat.app.service.AccountService;
import com.chat.app.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Lazy
    @Autowired
    private RelationshipService relationshipService;


    @Override
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public AccountResponse getAccountInfo(Long accountId) throws ChatException {
        Account account = getAccount(accountId);
        AccountResponse response = new AccountResponse();
        response.setAccountId(account.getAccountId());
        response.setUsername(account.getUsername());
        response.setAvatar(account.getAvatar());
        List<AccountResponse.FriendResponse> friends = relationshipService.getFriendsList(accountId).stream()
                .map(friend -> {
                    AccountResponse.FriendResponse friendResponse = new AccountResponse.FriendResponse();
                    friendResponse.setFriendId(friend.getFriendId());
                    friendResponse.setUsername(friend.getUsername());
                    friendResponse.setAvatar(friend.getAvatar());
                    return friendResponse;
                })
                .toList();
        response.setFriends(friends);
        return response;
    }

    @Override
    public Account getAccount(Long accountId) throws ChatException {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ChatException("Account not found"));
    }

    @Override
    public Account getAccount(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Account getAccount(String username, String password) throws ChatException {
        List<Account> accounts = searchAccounts(username);
        for (Account account : accounts) {
            if (passwordEncoder.matches(password, account.getPassword())) {
                return account;
            }
        }
        accounts.forEach(account -> {
            if (passwordEncoder.matches(password, account.getPassword())) {
                return ;
            }
        });
        throw new ChatException("Invalid username or password");
    }

    @Override
    public List<Account> searchAccounts(String username) throws ChatException {
        return accountRepository.findByUsername(username);
    }

    @Override
    public void updateAccount(Long accountId, AccountDTO accountDto) throws ChatException {
        Account account = getAccount(accountId);
        if (account == null) {
            throw new ChatException("Account not found");
        }
        if (accountDto.getAvatar() != null) {
            account.setAvatar(accountDto.getAvatar());
        }
        if (accountDto.getBirthdate() != null) {
            account.setBirthDate(accountDto.getBirthdate());
        }
        if (accountDto.getGender() != null) {
            account.setGender(accountDto.getGender());
        }
        if (accountDto.getBio() != null) {
            account.setBio(accountDto.getBio());
        }
        accountRepository.save(account);
    }

    @Override
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }


}
