package com.chat.app.security;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AccountService accountService;

//    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000;
    private static final int MAX_TOKENS_PER_ACCOUNT = 10;

    public void saveRefreshToken(String refreshToken, Account account) {
        Instant expiryDate = Instant.now().plusMillis(TokenProvider.REFRESH_TOKEN_EXPIRATION_TIME);
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


    public List<RefreshTokenEntity> getRefreshTokenByAccount(Long accountId) throws ChatException {
        Account account = accountService.findAccount(accountId);
        return refreshTokenRepository.findByAccount(account);
    }

    public Optional<String> getLatestRefreshTokenByAccount(Long accountId) throws ChatException {
        List<RefreshTokenEntity> refreshTokens = getRefreshTokenByAccount(accountId);
        return refreshTokens.stream()
                .sorted((t1, t2) -> t2.getExpiryDate().compareTo(t1.getExpiryDate()))
                .map(RefreshTokenEntity::getToken)
                .findFirst();
    }

    public void limitRefreshTokensPerAccount(Account account) {
        List<RefreshTokenEntity> tokens = refreshTokenRepository.findByAccount(account);
        if (tokens.size() > MAX_TOKENS_PER_ACCOUNT) {
            tokens.sort(Comparator.comparing(RefreshTokenEntity::getExpiryDate));
            RefreshTokenEntity oldestToken = tokens.getFirst();
            refreshTokenRepository.delete(oldestToken);
        }
    }


}