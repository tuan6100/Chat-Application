package com.chat.app.security;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AccountService accountService;

    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000;

    public void saveRefreshToken(String refreshToken, Account account) {
        Instant expiryDate = Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME);
        RefreshTokenEntity tokenEntity = new RefreshTokenEntity(refreshToken, account, expiryDate);
        refreshTokenRepository.save(tokenEntity);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        Optional<RefreshTokenEntity> token = refreshTokenRepository.findByToken(refreshToken);
        return token.map(refreshTokenEntity -> refreshTokenEntity.getExpiryDate().isAfter(Instant.now())).orElse(false);
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }


    public Optional<RefreshTokenEntity> getRefreshTokenByAccount(Long accountId) throws ChatException {
        return refreshTokenRepository.findByAccount(accountService.findAccount(accountId));
    }
}