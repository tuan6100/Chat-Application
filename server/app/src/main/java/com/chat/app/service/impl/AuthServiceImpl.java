package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.security.RefreshTokenService;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.AccountService;
import com.chat.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Override
    public ResponseEntity<String> login(AuthRequestWithUsername authRequest, HttpServletResponse response) throws ChatException {
        List<Account> accounts = accountService.searchAccounts(authRequest.getUsername());
        if (accounts.isEmpty()) {
            throw new ChatException("Invalid username");
        }
        for (Account account : accounts) {
            if (passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
                Authentication auth = new UsernamePasswordAuthenticationToken(account.getEmail(), authRequest.getPassword());
                SecurityContextHolder.getContext().setAuthentication(auth);
                String accessToken = tokenProvider.generateAccessToken(auth);
                response.setHeader("Authorization", "Bearer " + accessToken);
                Optional<String> optionalRefreshToken = refreshTokenService.getLatestRefreshTokenByAccount(account.getAccountId());
                String refreshToken;
                optionalRefreshToken.ifPresent(s -> refreshTokenService.deleteRefreshToken(s));
                refreshToken = tokenProvider.generateRefreshToken(auth);
                refreshTokenService.saveRefreshToken(refreshToken, account);
                refreshTokenService.limitRefreshTokensPerAccount(account);
                return ResponseEntity.ok("Login successful");
            }
        }
        throw new ChatException("Password does not match");
    }

    @Override
    public ResponseEntity<String> login(AuthRequestWithEmail authRequest, HttpServletResponse response) throws ChatException {
        Account account = accountService.getAccount(authRequest.getEmail());
        if (account == null) {
            throw new ChatException("Invalid email");
        }
        if (!passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
            throw new ChatException("Password does not match");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = tokenProvider.generateAccessToken(auth);
        response.setHeader("Authorization", "Bearer " + accessToken);
        Optional<String> optionalRefreshToken = refreshTokenService.getLatestRefreshTokenByAccount(account.getAccountId());
        String refreshToken;
        optionalRefreshToken.ifPresent(s -> refreshTokenService.deleteRefreshToken(s));
        refreshToken = tokenProvider.generateRefreshToken(auth);
        refreshTokenService.saveRefreshToken(refreshToken, account);
        refreshTokenService.limitRefreshTokensPerAccount(account);
        return ResponseEntity.ok("Login successful");
    }

    @Override
    public ResponseEntity<String> register(Account account, HttpServletResponse response) throws ChatException {
        String email = account.getEmail();
        String password = account.getPassword();
        if (accountService.getAccount(email) != null) {
            throw new ChatException("This email is used with another account");
        }
        account.setPassword(passwordEncoder.encode(password));
        Account savedAccount = accountService.createAccount(account);
        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        String accessToken = tokenProvider.generateAccessToken(auth);
        response.setHeader("Authorization", "Bearer " + accessToken);
        Optional<String> optionalRefreshToken = refreshTokenService.getLatestRefreshTokenByAccount(savedAccount.getAccountId());
        String refreshToken;
        optionalRefreshToken.ifPresent(s -> refreshTokenService.deleteRefreshToken(s));
        refreshToken = tokenProvider.generateRefreshToken(auth);
        refreshTokenService.saveRefreshToken(refreshToken, savedAccount);
        refreshTokenService.limitRefreshTokensPerAccount(savedAccount);
        return ResponseEntity.ok("Account created successfully");
    }

    @Override
    public ResponseEntity<String> logout(HttpServletRequest request) throws ChatException {
        removeAccessTokenFromHeader(request);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ChatException("User is not authenticated");
        }
        String email = auth.getName();
        Account account =  accountService.getAccount(email);
        String refreshToken = refreshTokenService.getLatestRefreshTokenByAccount(account.getAccountId()).get();
        if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
            refreshTokenService.deleteRefreshToken(refreshToken);
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }
    }

    private void removeAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.replace(bearerToken, "");
        }
    }
}

