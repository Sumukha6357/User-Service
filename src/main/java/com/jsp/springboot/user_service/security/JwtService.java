package com.jsp.springboot.user_service.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.jsp.springboot.user_service.auth.SecurityUsersProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecurityUsersProperties props;

    public JwtService(SecurityUsersProperties props) {
        this.props = props;
    }

    public String generateToken(String username, String role) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(props.getJwtExpirationSeconds());
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(getSigningKey())
            .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public long getExpirationSeconds() {
        return props.getJwtExpirationSeconds();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(props.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }
}
