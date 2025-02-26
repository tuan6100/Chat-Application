package com.chat.app.service.implementations.user;

import com.chat.app.exception.UnauthorizedException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequest;
import com.chat.app.payload.request.ResetPasswordRequest;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.repository.jpa.AccountRepository;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.interfaces.user.auth.RefreshTokenService;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import com.chat.app.service.interfaces.user.information.AccountService;
import com.chat.app.service.interfaces.user.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private AccountService accountService;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Override
    public HttpHeaders getResponseHeader(Authentication auth, Account account) {
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
    public AuthResponse login(AuthRequest authRequest) throws UnauthorizedException {
        Account account = accountSearchService.searchAccountByEmail(authRequest.getEmail());
        if (account == null) {
            throw new UnauthorizedException("Invalid email");
        }
        if (!passwordEncoder.matches(authRequest.getPassword(), account.getPassword())) {
            throw new UnauthorizedException("Password does not match");
        }
        Authentication auth = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        HttpHeaders responseHeader = getResponseHeader(auth, account);
        return new AuthResponse("Login successful", HttpStatus.OK.value(), account.getAccountId(), responseHeader);
    }

    @Override
    public AuthResponse register(Account account) throws UnauthorizedException {
        String email = account.getEmail();
        String password = account.getPassword();
        if (accountSearchService.searchAccountByEmail(email) != null) {
            throw new UnauthorizedException("This email is used with another account");
        }
        account.setPassword(passwordEncoder.encode(password));
        Account savedAccount = accountService.createAccount(account);
        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        HttpHeaders responseHeader = getResponseHeader(auth, savedAccount);
        return new AuthResponse("Account created successfully", HttpStatus.CREATED.value(), savedAccount.getAccountId(), responseHeader);
    }

    @Override
    public Account resetPassword(ResetPasswordRequest resetPasswordRequest) throws UnauthorizedException {
        String email = resetPasswordRequest.getEmail();
        String oldPassword = resetPasswordRequest.getOldPassword();
        String newPassword = resetPasswordRequest.getNewPassword();
        Account account = accountSearchService.searchAccountByEmail(email);
        if (account == null) {
            throw new UnauthorizedException("Invalid email");
        }
        if (!passwordEncoder.matches(oldPassword, account.getPassword())) {
            throw new UnauthorizedException("Old password does not match");
        }
        account.setPassword(passwordEncoder.encode(newPassword));
        return accountRepository.save(account);
    }

    @Override
    public AuthResponse updatePassword(AuthRequest authRequest) throws UnauthorizedException {
        Account account = accountSearchService.searchAccountByEmail(authRequest.getEmail());
        String newPassword = authRequest.getPassword();
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        account.getRefreshTokens().forEach(refreshToken -> refreshTokenService.deleteRefreshToken(refreshToken.getToken()));
        return login(new AuthRequest(authRequest.getEmail(), newPassword));
    }


}
