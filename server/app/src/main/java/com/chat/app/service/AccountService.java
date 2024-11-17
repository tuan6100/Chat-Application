package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

    public Account createAccount(Account account) throws ChatException;

    public Account findAccount(Long id) throws ChatException;

    public Account findAccount(String email);

    public Account findAccount(String username, String password) throws ChatException;

    public List<Account> searchAccounts(String username) throws ChatException;

    public Account updateAccount(Long accountId, AccountDTO accountDto) throws ChatException;

    public Account resetPassword(Long accountId, String oldPassword, String newPassword) throws ChatException;

    public void deleteAccount(Long accountId);

    public List<Account> searchAccount(String query);

}