package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService {

    public Account findAccount(Long id) throws ChatException;

    public Account findAccountProfile(String jwt);

    public Account createAccount(Account account) throws ChatException;

    public Account updateAccount(Long accountId, AccountDTO accountDto) throws ChatException;

    public void deleteAccount(Long accountId);

    public List<Account> searchAccount(String query);

}
