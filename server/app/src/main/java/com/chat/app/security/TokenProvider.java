package com.chat.app.security;

import com.nimbusds.oauth2.sdk.auth.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class TokenProvider {
    private SecretKey key = Keys.hmacShaKeyFor(JwtTokenValidator.JWT_SECRET.getBytes());

    public String generateToken(Authentication authentication) {
        String jwt = Jwts.builder().setIssuer("ChatApp")
                .setSubject(authentication.getName())
                .claim("email", authentication.getName())
                .signWith(key)
                .compact();
        return jwt;
    }

    public String getEmailFromToken(String jwt) {
        jwt = jwt.substring(7);
        Claims claim = Jwts.parser().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        String email = String.valueOf(claim.get("email"));
        return email;
    }
}
