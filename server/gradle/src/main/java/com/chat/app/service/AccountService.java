package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.dto.AccountDTO;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.response.AccountResponse;
import com.chat.app.payload.response.ChatResponse;
import com.chat.app.payload.response.PrivateChatResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface AccountService {

    Account createAccount(Account account);

    Account getAccount(Long id) throws ChatException;

    Account getAccount(String email);

    AccountResponse getAccountResponse(Long accountId) throws ChatException;

    void updateAccount(Long accountId, AccountDTO accountDto) throws ChatException;

    void deleteAccount(Long accountId);

    void markUserOnline(Long accountId);

    void markUserOffline(Long accountId);

    boolean isUserOnline(Long accountId);

    Date getLastOnlineTime(Long accountId);

    List<PrivateChatResponse> getAllPrivateChat(Long accountId);

    List<ChatResponse> getAllChatsByAccountId(Long accountId);

}
