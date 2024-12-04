package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequestWithEmail;
import com.chat.app.payload.request.AuthRequestWithUsername;
import com.chat.app.payload.request.ResetPasswordRequest;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.security.RefreshTokenService;
import com.chat.app.security.TokenProvider;
import com.chat.app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<AuthResponse> loginWithUsername(@RequestBody AuthRequestWithUsername authRequest) throws ChatException {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok()
                .headers(authResponse.getHeaders())
                .body(authResponse);
    }

    @PostMapping("/login/email")
    public ResponseEntity<AuthResponse> loginWithEmail(@RequestBody AuthRequestWithEmail authRequest) throws ChatException {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok()
                .headers(authResponse.getHeaders())
                .body(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody Account account) throws ChatException {
        AuthResponse authResponse = authService.register(account);
        return ResponseEntity.ok()
                .headers(authResponse.getHeaders())
                .body(authResponse);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<AuthResponse> logout(HttpServletRequest request) throws ChatException {
        return ResponseEntity.ok(authService.logout(request));
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) throws ChatException {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(email, newPassword);
        return ResponseEntity.ok(authService.getNewPassword(resetPasswordRequest));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws ChatException {
        Account account = authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(Map.of("message", "Reset password successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) throws ChatException {
        String refreshToken = request.get("refreshToken");
        if (refreshTokenService.isRefreshTokenValid(refreshToken)) {
            String newAccessToken = refreshTokenService.getAccessTokenByRefreshToken(refreshToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }

}
