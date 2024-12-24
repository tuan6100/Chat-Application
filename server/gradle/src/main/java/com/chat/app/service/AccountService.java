package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

    Account createAccount(Account account);

    Account getAccount(Long id) throws ChatException;

    Account getAccount(String email);

    void updateAccount(Long accountId, AccountDTO accountDto) throws ChatException;

    void deleteAccount(Long accountId);


}
