package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody Account account) throws ChatException {
        return authService.register(account);
    }

    @PostMapping("/login/email")
    public ResponseEntity<AuthResponse>  login(@RequestBody AuthRequestWithEmail authRequest) throws ChatException {
        return authService.login(authRequest);
    }

    @PostMapping("/login/username")
    public ResponseEntity<AuthResponse>  login(@RequestBody AuthRequestWithUsername authRequest) throws ChatException {
        return authService.login(authRequest);
    }


}
