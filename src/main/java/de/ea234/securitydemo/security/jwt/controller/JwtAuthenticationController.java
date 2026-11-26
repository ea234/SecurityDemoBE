package de.ea234.securitydemo.security.jwt.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.ea234.securitydemo.security.jwt.dto.JwtLoginRequest;
import de.ea234.securitydemo.security.jwt.dto.JwtLoginResponse;
import de.ea234.securitydemo.security.jwt.service.JwtService;

@RestController
@Profile("jpa")
@RequestMapping("/api/auth")
public class JwtAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public JwtAuthenticationController(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtLoginResponse> login(
            @RequestBody JwtLoginRequest request) {

        Authentication authentication =
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.username(),
                    request.password()
                )
            );

        String token = jwtService.generateToken(
            (org.springframework.security.core.userdetails.UserDetails)
                authentication.getPrincipal()
        );

        JwtLoginResponse response = new JwtLoginResponse(
            token,
            "Bearer",
            3600
        );

        return ResponseEntity.ok(response);
    }
}
