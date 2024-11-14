package com.chat.app.service;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    public ResponseEntity<AuthResponse> login(AuthRequestWithUsername authRequest) throws ChatException;
    public ResponseEntity<AuthResponse> login(AuthRequestWithEmail authRequest) throws ChatException;
    public ResponseEntity<AuthResponse> register(Account account) throws ChatException;

}
