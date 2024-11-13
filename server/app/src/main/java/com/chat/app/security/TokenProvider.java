package com.chat.app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.stereotype.Service;

@Service
public class TokenProvider {

    public String generateToken(Authentication authentication) {
        String jwt = Jwts.builder().setIssuer()
    }
}
