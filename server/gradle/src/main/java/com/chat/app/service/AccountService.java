package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.dto.AccountDTO;
import com.chat.app.model.elasticsearch.AccountIndex;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.response.AccountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

    public Account createAccount(Account account);

    public AccountResponse getAccountInfo(Long accountId) throws ChatException;

    public Account getAccount(Long id) throws ChatException;

    public Account getAccount(String email);

    public Page<AccountIndex> searchAccount(String username, Pageable pageable);

    public void updateAccount(Long accountId, AccountDTO accountDto) throws ChatException;


    public void deleteAccount(Long accountId);


}
