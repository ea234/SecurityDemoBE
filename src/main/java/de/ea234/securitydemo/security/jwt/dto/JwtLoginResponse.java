package de.ea234.securitydemo.security.jwt.dto;

public record JwtLoginResponse(
    String token,
    String tokenType,
    long expiresIn
) {
}
