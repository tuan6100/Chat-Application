package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.request.ResetPasswordRequest;
import com.chat.app.payload.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AuthService {

    public HttpHeaders getResponseHeader(Authentication auth, Account account);

    public AuthResponse login(AuthRequestWithEmail authRequest) throws ChatException;

    public AuthResponse register(Account account) throws ChatException;

    public AuthResponse updatePassword(AuthRequestWithEmail authRequest) throws ChatException;

    public Account resetPassword(ResetPasswordRequest resetPasswordRequest) throws ChatException;
}