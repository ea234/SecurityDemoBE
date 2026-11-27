package de.ea234.securitydemo.security.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

import de.ea234.securitydemo.security.jwt.service.JwtService;
import org.springframework.security.core.GrantedAuthority;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    private static final String SECRET =
        "L1OldGlHsThfpkNBsbcleBQw+rmp3kvsM0EaZPI4Qpk=";

    private static final long EXPIRATION_MS = 3600000;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(SECRET, EXPIRATION_MS);

        userDetails = mock(UserDetails.class);

        when(userDetails.getUsername())
            .thenReturn("testuser");

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        doReturn(authorities)
        .when(userDetails)
        .getAuthorities();        
    }

    @Test
    void generateTokenShouldCreateNonEmptyToken() {

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateTokenShouldContainCorrectSubject() {

        String token = jwtService.generateToken(userDetails);

        Claims claims = jwtService.extractClaims(token);

        assertEquals("testuser", claims.getSubject());
    }

    @Test
    void generateTokenShouldContainAuthorities() {

        String token = jwtService.generateToken(userDetails);

        Claims claims = jwtService.extractClaims(token);

        List<?> authorities = claims.get("authorities", List.class);

        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
    }

    
    @Test
    void generateTokenShouldContainIssuedAtAndExpiration() {

        long beforeGeneration = System.currentTimeMillis();

        String token = jwtService.generateToken(userDetails);

        long afterGeneration = System.currentTimeMillis();

        Claims claims = jwtService.extractClaims(token);

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertNotNull(issuedAt);
        assertNotNull(expiration);

        assertTrue(
            issuedAt.getTime() >= beforeGeneration - 1000
        );

        assertTrue(
            issuedAt.getTime() <= afterGeneration
        );

        assertTrue(expiration.after(issuedAt));

        long actualExpiration = expiration.getTime()
            - issuedAt.getTime();

        assertTrue(
            Math.abs(actualExpiration - EXPIRATION_MS) < 1000,
            "Ablaufzeit weicht um mindestens eine Sekunde ab"
        );
    }
    

    @Test
    void generatedTokenShouldBeValidForCorrectUser() {

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void generatedTokenShouldBeInvalidForDifferentUser() {

        String token = jwtService.generateToken(userDetails);

        UserDetails differentUser = mock(UserDetails.class);

        when(differentUser.getUsername())
            .thenReturn("differentuser");

        assertFalse(
            jwtService.isTokenValid(token, differentUser)
        );
    }

    @Test
    void extractClaimsShouldRejectInvalidSignature() {

        String token = jwtService.generateToken(userDetails);

        JwtService anotherJwtService = new JwtService(
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            EXPIRATION_MS
        );

        assertThrows(
            Exception.class,
            () -> anotherJwtService.extractClaims(token)
        );
    }
}
