package de.ea234.securitydemo.security.jwt.dto;

public record JwtLoginRequest(
    String username,
    String password
) {
}
