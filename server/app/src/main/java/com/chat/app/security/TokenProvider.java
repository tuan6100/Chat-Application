package com.chat.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenProvider {
    private final SecretKey key = Keys.hmacShaKeyFor(JwtTokenValidator.JWT_SECRET.getBytes());
    private static final long EXPIRATION_TIME = 3600000;

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setIssuer("ChatApp")
                .setSubject(authentication.getName())
                .claim("username", authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String jwt) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
        return claims.get("username", String.class);
    }
}
