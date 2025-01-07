package com.chat.app.controller;

import com.chat.app.exception.ChatException;
import com.chat.app.exception.UnauthorizedException;
import com.chat.app.model.entity.Account;
import com.chat.app.payload.request.AuthRequest;
import com.chat.app.payload.request.ResetPasswordRequest;
import com.chat.app.payload.response.AccountValidationResponse;
import com.chat.app.payload.response.AuthResponse;
import com.chat.app.security.RefreshTokenService;
import com.chat.app.service.AccountService;
import com.chat.app.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginWithEmail(@RequestBody AuthRequest authRequest) throws UnauthorizedException {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok()
                .headers(authResponse.getHeaders())
                .body(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody Account account) throws UnauthorizedException {
        AuthResponse authResponse = authService.register(account);
        return ResponseEntity.ok()
                .headers(authResponse.getHeaders())
                .body(authResponse);
    }

    @GetMapping("forgot-password/validate-account")
    public ResponseEntity<AccountValidationResponse> validateAccount(@RequestParam String email) throws UnauthorizedException {
        Account account = accountService.getAccount(email);
        if (account == null) {
            throw new UnauthorizedException("Account not found");
        }
        return ResponseEntity.ok(new AccountValidationResponse(account.getUsername(), account.getAvatar()));
    }

    @PutMapping("/forgot-password/renew-password")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody Map<String, String> request) throws UnauthorizedException {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        AuthResponse authResponse = authService.updatePassword(new AuthRequest(email, newPassword));
        return ResponseEntity.ok()
                .headers(authResponse.getHeaders())
                .body(authResponse);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws UnauthorizedException {
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
