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
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey signingKey;

    // Inicializa la clave de firma después de que se inyecten las propiedades
    @PostConstruct
    void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera un token JWT para el usuario proporcionado
    public String generateToken(UserDetails userDetails) {

        String rol = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow()
                .replace("ROLE_", "");

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("rol", rol)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtExpiration)))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(refreshExpiration)))
                .signWith(signingKey)
                .compact();
    }

    // Extrae el nombre de usuario del token JWT
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extrae el rol del token JWT
    public String extractRol(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    // Valida el token verificando su firma y expiración
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extrae todos los claims del token JWT
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}