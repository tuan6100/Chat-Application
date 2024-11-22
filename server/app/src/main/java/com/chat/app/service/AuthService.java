package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    public ResponseEntity<String> login(AuthRequestWithUsername authRequest, HttpServletResponse response) throws ChatException;

    public ResponseEntity<String> login(AuthRequestWithEmail authRequest, HttpServletResponse response) throws ChatException;

    public ResponseEntity<String> register(Account account, HttpServletResponse response) throws ChatException;

    public ResponseEntity<String> logout(HttpServletRequest request) throws ChatException;
}
