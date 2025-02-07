package com.chat.app.service.interfaces.user.information;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AccountRequest;
import com.chat.app.payload.response.AccountResponse;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface AccountService {

    Account createAccount(Account account);

    AccountResponse getAccountResponse(Long accountId) throws ChatException;

    void updateAccount(Long accountId, AccountRequest accountRequest) throws ChatException;

    void deleteAccount(Long accountId);

    void markUserOnline(Long accountId);

    void markUserOffline(Long accountId);

    boolean isUserOnline(Long accountId);

    Date getLastOnlineTime(Long accountId);

}
