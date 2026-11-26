package de.ea234.securitydemo.security.jwt.service;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
@Profile("jpa")
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs) {

        this.secretKey = Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(secret)
        );

        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) {

        List<String> authorities = userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        Date issuedAt = new Date();
        Date expiration = new Date(
            issuedAt.getTime() + expirationMs
        );

        return Jwts.builder()
            .subject(userDetails.getUsername())
            .claim("authorities", authorities)
            .issuedAt(issuedAt)
            .expiration(expiration)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        Claims claims = extractClaims(token);

        return claims.getSubject().equals(userDetails.getUsername())
            && claims.getExpiration().after(new Date());
    }
}
