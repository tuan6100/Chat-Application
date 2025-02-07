package com.chat.app.service.interfaces.user.auth;

import com.chat.app.exception.UnauthorizedException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequest;
import com.chat.app.payload.request.ResetPasswordRequest;
import com.chat.app.payload.response.AuthResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;



@Service
public interface AuthService {

    public HttpHeaders getResponseHeader(Authentication auth, Account account);

    public AuthResponse login(AuthRequest authRequest) throws UnauthorizedException;

    public AuthResponse register(Account account) throws UnauthorizedException;

    public AuthResponse updatePassword(AuthRequest authRequest) throws UnauthorizedException;

    public Account resetPassword(ResetPasswordRequest resetPasswordRequest) throws UnauthorizedException;
}