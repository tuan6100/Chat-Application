package com.chat.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public final class TokenProvider {

    private final SecretKey key = Keys.hmacShaKeyFor(MySecretKey.JWT_SECRET.getBytes());
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 3600L * 1000;
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 3600L * 24 * 30 * 1000;


    public String generateAccessToken(@Nonnull Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Jwts.builder()
                .issuer("ChatApp")
                .subject(authentication.getPrincipal().toString())
                .claim("email", authentication.getPrincipal().toString())
                .claim("authorities", String.join(",", authorities))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()  + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(@Nonnull Authentication authentication) {
        return Jwts.builder()
                .issuer("ChatApp")
                .subject(authentication.getPrincipal().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()  + REFRESH_TOKEN_EXPIRATION_TIME))
                .claim("refresh_token_id", UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String getEmailFromToken(String jwt) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
        return claims.get("email", String.class);
    }
}