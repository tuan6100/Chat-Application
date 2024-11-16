package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login/username")
    public ResponseEntity<AuthResponse> loginWithUsername(@RequestBody AuthRequestWithUsername authRequest) throws ChatException {
        return authService.login(authRequest);
    }

    @PostMapping("/login/email")
    public ResponseEntity<AuthResponse> loginWithEmail(@RequestBody AuthRequestWithEmail authRequest) throws ChatException {
        return authService.login(authRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody Account account) throws ChatException {
        AuthResponse authResponse = authService.register(account);
        return ResponseEntity
                .status(201)
                .header("Location", "/api/account/" + account.getAccountId())
                .body(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
