package com.meevent.webapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private SecretKey signingKey;

    // Initializes the signing key after properties are injected
    @PostConstruct
    void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Generates an access JWT for the given user
    public String generateToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow()
                .replace("ROLE_", "");

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpiration)))
                .signWith(signingKey)
                .compact();
    }

    // Generates a refresh JWT for the given user
    public String generateRefreshToken(UserDetails userDetails) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshTokenExpiration)))
                .signWith(signingKey)
                .compact();
    }

    // Extracts username (email) from JWT
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extracts user role from JWT
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Validates JWT signature and expiration
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            LOGGER.warn("Invalid JWT token", e);
            return false;
        }
    }

    // Extracts all claims from JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}