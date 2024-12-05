package com.chat.app.service.impl;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.request.ResetPasswordRequest;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.repository.AccountRepository;
import com.chat.app.security.RefreshTokenService;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.AccountService;
import com.chat.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Override
    public HttpHeaders getResponseHeader(Authentication auth, Account account) throws ChatException {
        HttpHeaders responseHeader = new HttpHeaders();
        SecurityContextHolder.getContext().setAuthentication(auth);
        String accessToken = tokenProvider.generateAccessToken(auth);
        responseHeader.set("Authorization", "Bearer " + accessToken);
        String refreshToken;
        refreshToken = tokenProvider.generateRefreshToken(auth);
        refreshTokenService.saveRefreshToken(refreshToken, account);
        responseHeader.add("X-Refresh-Token", "Bearer " + refreshToken);
        refreshTokenService.limitRefreshTokensPerAccount(account);
        return responseHeader;
    }

    @Override
    public AuthResponse login(AuthRequestWithUsername authRequest) throws ChatException {
        List<Account> accounts = accountService.searchAccounts(authRequest.getUsername());
        if (accounts.isEmpty()) {
            throw new ChatException("Invalid username");
        }
        for (Account account : accounts) {
            if (passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
                Authentication auth = new UsernamePasswordAuthenticationToken(account.getEmail(), authRequest.getPassword());
                HttpHeaders responseHeader = getResponseHeader(auth, account);
                return new AuthResponse("Login successful", HttpStatus.OK.value(), responseHeader);
            }
        }
        throw new ChatException("Password does not match");
    }

    @Override
    public AuthResponse login(AuthRequestWithEmail authRequest) throws ChatException {
        Account account = accountService.getAccount(authRequest.getEmail());
        if (account == null) {
            throw new ChatException("Invalid email");
        }
        if (!passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
            throw new ChatException("Password does not match");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        HttpHeaders responseHeader = getResponseHeader(auth, account);
        return new AuthResponse("Login successful", HttpStatus.OK.value(), responseHeader);
    }

    @Override
    public AuthResponse register(Account account) throws ChatException {
        String email = account.getEmail();
        String password = account.getPassword();
        if (accountService.getAccount(email) != null) {
            throw new ChatException("This email is used with another account");
        }
        account.setPassword(passwordEncoder.encode(password));
        Account savedAccount = accountService.createAccount(account);
        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        HttpHeaders responseHeader = getResponseHeader(auth, savedAccount);
        return new AuthResponse("Account created successfully", HttpStatus.CREATED.value(), responseHeader);
    }



    @Override
    public Account resetPassword(ResetPasswordRequest resetPasswordRequest) throws ChatException {
        String email = resetPasswordRequest.getEmail();
        String oldPassword = resetPasswordRequest.getOldPassword();
        String newPassword = resetPasswordRequest.getNewPassword();
        Account account = accountService.getAccount(email);
        if (account == null) {
            throw new ChatException("Invalid email");
        }
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new ChatException("Old password does not match");
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        return accountRepository.save(account);
    }

    @Override
    public AuthResponse logout(HttpServletRequest request) throws ChatException {
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
            return new AuthResponse("Logout successful", HttpStatus.OK.value(), null);
        } else {
            return new AuthResponse("Logout failed", HttpStatus.BAD_REQUEST.value(), null);
        }
    }

    @Override
    public AuthResponse updatePassword(AuthRequestWithEmail authRequest) throws ChatException {
        Account account = accountService.getAccount(authRequest.getEmail());
        String newPassword = authRequest.getPassword();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        account.getRefreshTokens().forEach(refreshToken -> refreshTokenService.deleteRefreshToken(refreshToken.getToken()));
        return login(new AuthRequestWithEmail(authRequest.getEmail(), newPassword));
    }

    private void removeAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.replace(bearerToken, "");
        }
    }

}
