package com.chat.app.service.implementations.user;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.RefreshToken;
import com.chat.app.repository.jpa.RefreshTokenRepository;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.interfaces.user.auth.RefreshTokenService;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.user.information.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AccountSearchService accountSearchService;

    private static final int MAX_TOKENS_PER_ACCOUNT = 10;


    @Override
    public void saveRefreshToken(String refreshToken, Account account) {
        Instant expiryDate = Instant.now().plusMillis(TokenProvider.REFRESH_TOKEN_EXPIRATION_TIME);
        RefreshToken tokenEntity = new RefreshToken(refreshToken, account, expiryDate, Instant.now());
        refreshTokenRepository.save(tokenEntity);
    }

    @Override
    public boolean isRefreshTokenValid(String refreshToken) throws ChatException {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            if (token.get().getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.deleteByToken(refreshToken);
                return false;
            }
            return true;
        }
        throw new ChatException("Invalid refresh token");
    }

    @Override
    public List<RefreshToken> getRefreshTokenByAccount(Long accountId) throws ChatException {
        Account account = accountSearchService.searchAccountById(accountId);
        return refreshTokenRepository.findByAccount(account);
    }

    @Override
    public Account getAccountByRefreshToken(String refreshToken) throws ChatException {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isPresent()) {
            return token.get().getAccount();
        }
        throw new ChatException("Invalid refresh token");
    }

    @Override
    public String getAccessTokenByRefreshToken(String refreshToken) throws ChatException {
        Account account = getAccountByRefreshToken(refreshToken);
        Authentication authentication = new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword());
        return tokenProvider.generateAccessToken(authentication);
    }

    @Override
    public void limitRefreshTokensPerAccount(Account account) {
        List<RefreshToken> tokens = refreshTokenRepository.findByAccount(account);
        if (tokens.size() > MAX_TOKENS_PER_ACCOUNT) {
            tokens.sort(Comparator.comparing(RefreshToken::getExpiryDate));
            RefreshToken oldestToken = tokens.getFirst();
            refreshTokenRepository.delete(oldestToken);
        }
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }


}
