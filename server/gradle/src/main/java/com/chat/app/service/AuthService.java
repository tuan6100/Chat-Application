package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    public HttpHeaders getResponseHeader(Authentication auth, Account account) throws ChatException;

    public AuthResponse login(AuthRequestWithUsername authRequestr) throws ChatException;

    public AuthResponse login(AuthRequestWithEmail authRequest) throws ChatException;

    public AuthResponse register(Account account) throws ChatException;

    public AuthResponse logout(HttpServletRequest request) throws ChatException;
}
