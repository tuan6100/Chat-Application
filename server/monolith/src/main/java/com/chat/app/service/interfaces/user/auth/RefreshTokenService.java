package com.chat.app.service.interfaces.user.auth;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.RefreshToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RefreshTokenService {

    void saveRefreshToken(String refreshToken, Account account);

    boolean isRefreshTokenValid(String refreshToken) throws ChatException;

    List<RefreshToken> getRefreshTokenByAccount(Long accountId) throws ChatException;

    Account getAccountByRefreshToken(String refreshToken) throws ChatException;

    public String getAccessTokenByRefreshToken(String refreshToken) throws ChatException;

    public void limitRefreshTokensPerAccount(Account account);

    public void deleteRefreshToken(String refreshToken);


}
