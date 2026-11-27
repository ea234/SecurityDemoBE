package de.ea234.securitydemo.security.oauth2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.ea234.securitydemo.security.oauth2.entity.OAuth2Benutzer;

@Repository
public interface OAuth2BenutzerRepository
        extends JpaRepository<OAuth2Benutzer, Integer> {

    Optional<OAuth2Benutzer> findByProviderAndProviderUserId(
            String provider,
            String providerUserId
    );

    boolean existsByProviderAndProviderUserId(
            String provider,
            String providerUserId
    );
}
