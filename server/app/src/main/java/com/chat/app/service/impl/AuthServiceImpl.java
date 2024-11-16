package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.repository.AccountRepository;
import com.chat.app.security.RefreshTokenService;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Override
    public ResponseEntity<AuthResponse> login(AuthRequestWithUsername authRequest) throws ChatException {
        Account account = accountRepository.findByUsername(authRequest.getUsername());
        if (account == null || !passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
            throw new ChatException("Invalid username or password");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);
        refreshTokenService.saveRefreshToken(refreshToken, account);
        AuthResponse response = new AuthResponse(accessToken, refreshToken, true);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequestWithEmail authRequest) throws ChatException {
        Account account = accountRepository.findByEmail(authRequest.getEmail());
        if (account == null || !passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
            throw new ChatException("Invalid email or password");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(account.getUsername(), authRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);
        refreshTokenService.saveRefreshToken(refreshToken, account);
        AuthResponse response = new AuthResponse(accessToken, refreshToken, true);
        return ResponseEntity.ok(response);
    }

    @Override
    public AuthResponse register(Account account) throws ChatException {
        String username = account.getUsername();
        String email = account.getEmail();
        String password = account.getPassword();
        if (accountRepository.findByEmail(email) != null) {
            throw new ChatException("This email is used with another account");
        }
        if (accountRepository.findByUsername(username) != null) {
            throw new ChatException("This username is used with another account");
        }
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
        Authentication auth = new UsernamePasswordAuthenticationToken(account.getUsername(), account.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(auth);
        refreshTokenService.saveRefreshToken(refreshToken, account);
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

