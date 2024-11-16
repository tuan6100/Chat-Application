package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.repository.AccountRepository;
import com.chat.app.security.RefreshTokenService;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.AccountService;
import com.chat.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<AuthResponse> login(AuthRequestWithUsername authRequest) throws ChatException {
        Account account = accountService.findAccount(authRequest.getUsername(), authRequest.getPassword());
        if (account == null || !passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
            throw new ChatException("Invalid username or password");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(account.getEmail(), authRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = tokenProvider.generateAccessToken(auth);
        Optional<String> optionalRefreshToken = refreshTokenService.getLatestRefreshTokenByAccount(account.getAccountId());
        String refreshToken;
        if (optionalRefreshToken.isPresent()) {
            refreshToken = optionalRefreshToken.get();
        } else {
            refreshToken = tokenProvider.generateRefreshToken(auth);
            refreshTokenService.saveRefreshToken(refreshToken, account);
        }
        AuthResponse response = new AuthResponse(accessToken, refreshToken, true);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequestWithEmail authRequest) throws ChatException {
        Account account = accountService.findAccount(authRequest.getEmail());
        if (account == null || !passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
            throw new ChatException("Invalid email or password");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = tokenProvider.generateAccessToken(auth);
        Optional<String> optionalRefreshToken = refreshTokenService.getLatestRefreshTokenByAccount(account.getAccountId());
        String refreshToken;
        if (optionalRefreshToken.isPresent()) {
            refreshToken = optionalRefreshToken.get();
        } else {
            refreshToken = tokenProvider.generateRefreshToken(auth);
            refreshTokenService.saveRefreshToken(refreshToken, account);
        }
        AuthResponse response = new AuthResponse(accessToken, refreshToken, true);
        return ResponseEntity.ok(response);
    }

    @Override
    public AuthResponse register(Account account) throws ChatException {
        String email = account.getEmail();
        String password = account.getPassword();
        if (accountService.findAccount(email) != null) {
            throw new ChatException("This email is used with another account");
        }
        account.setPassword(passwordEncoder.encode(password));
        Account savedAccount = accountService.createAccount(account);
        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        String accessToken = tokenProvider.generateAccessToken(auth);
        Optional<String> optionalRefreshToken = refreshTokenService.getLatestRefreshTokenByAccount(savedAccount.getAccountId());
        String refreshToken;
        if (optionalRefreshToken.isPresent()) {
            refreshToken = optionalRefreshToken.get();
        } else {
            refreshToken = tokenProvider.generateRefreshToken(auth);
            refreshTokenService.saveRefreshToken(refreshToken, savedAccount);
        }
        refreshTokenService.limitRefreshTokensPerAccount(account);
        return new AuthResponse(accessToken, refreshToken, true);
    }

    @Override
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);
        if (refreshToken != null && refreshTokenService.isRefreshTokenValid(refreshToken)) {
            refreshTokenService.deleteRefreshToken(refreshToken);
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}

