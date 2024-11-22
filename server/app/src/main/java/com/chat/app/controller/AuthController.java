package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.security.RefreshTokenService;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/login/username")
    public ResponseEntity<String> loginWithUsername(@RequestBody AuthRequestWithUsername authRequest) throws ChatException {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        return authService.login(authRequest, response);
    }

    @PostMapping("/login/email")
    public ResponseEntity<String> loginWithEmail(@RequestBody AuthRequestWithEmail authRequest) throws ChatException {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        return authService.login(authRequest, response);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Account account) throws ChatException {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        return authService.register(account, response);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) throws ChatException {
        return authService.logout(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) throws ChatException {
        String refreshToken = request.get("refreshToken");
        if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
            String newAccessToken = refreshTokenService.getAccessTokenByRefreshToken(refreshToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }

}
