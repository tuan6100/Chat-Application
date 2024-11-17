package com.chat.app.security;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.service.AccountService;
import com.chat.app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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


    private static final int MAX_TOKENS_PER_ACCOUNT = 10;

    public void saveRefreshToken(String refreshToken, Account account) {
        Instant expiryDate = Instant.now().plusMillis(TokenProvider.REFRESH_TOKEN_EXPIRATION_TIME);
        RefreshTokenEntity tokenEntity = new RefreshTokenEntity(refreshToken, account, expiryDate);
        refreshTokenRepository.save(tokenEntity);
    }

    public boolean isRefreshTokenValid(String refreshToken) throws ChatException {
        Optional<RefreshTokenEntity> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            if (token.get().getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.deleteByToken(refreshToken);
                return false;
            }
            return true;
        }
        throw new ChatException("Invalid refresh token");
    }

    public List<RefreshTokenEntity> getRefreshTokenByAccount(Long accountId) throws ChatException {
        Account account = accountService.findAccount(accountId);
        return refreshTokenRepository.findByAccount(account);
    }

    public Account getAccountByRefreshToken(String refreshToken) throws ChatException {
        Optional<RefreshTokenEntity> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            return token.get().getAccount();
        }
        throw new ChatException("Invalid refresh token");
    }

    public String getAccessTokenByRefreshToken(String refreshToken) throws ChatException {
        Account account = getAccountByRefreshToken(refreshToken);
        Authentication authentication = new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword());
        return tokenProvider.generateAccessToken(authentication);
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

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

}