package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.response.AccountResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

    public Account createAccount(Account account);

    public AccountResponse getAccountInfo(Long accountId) throws ChatException;

    public Account getAccount(Long id) throws ChatException;

    public Account getAccount(String email);

    public Account getAccount(String username, String password) throws ChatException;

    public List<Account> searchAccounts(String username) throws ChatException;

    public void updateAccount(Long accountId, AccountDTO accountDto) throws ChatException;


    public void deleteAccount(Long accountId);


}
