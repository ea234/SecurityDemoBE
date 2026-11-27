
package de.ea234.securitydemo.security.oauth2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ea234.securitydemo.security.oauth2.entity.OAuth2Benutzer;
import de.ea234.securitydemo.security.oauth2.repository.OAuth2BenutzerRepository;

@Service
@Profile("jpa")
@Transactional(readOnly = true)
public class OAuth2BenutzerService {

    private final OAuth2BenutzerRepository repository;

    public OAuth2BenutzerService(
            OAuth2BenutzerRepository repository) {

        this.repository = repository;
    }

    // -------------------------------------------------
    // Benutzer suchen
    // -------------------------------------------------

    public Optional<OAuth2Benutzer> findByProviderAndProviderUserId(
            String provider,
            String providerUserId) {

        return repository.findByProviderAndProviderUserId(
                provider,
                providerUserId
        );
    }

    public Optional<OAuth2Benutzer> findById(Integer id) {
        return repository.findById(id);
    }

    public List<OAuth2Benutzer> findAll() {
        return repository.findAll();
    }

    public boolean existsByProviderAndProviderUserId(
            String provider,
            String providerUserId) {

        return repository.existsByProviderAndProviderUserId(
                provider,
                providerUserId
        );
    }

    public boolean isEnabled(
            String provider,
            String providerUserId) {

        return findByProviderAndProviderUserId(
                provider,
                providerUserId
        ).map(OAuth2Benutzer::isEnabled)
         .orElse(false);
    }

    // -------------------------------------------------
    // Benutzer aktivieren
    // -------------------------------------------------

    @Transactional
    public OAuth2Benutzer activate(Integer id) {

        OAuth2Benutzer benutzer = getRequiredUser(id);

        benutzer.setEnabled(true);

        return repository.save(benutzer);
    }

    // -------------------------------------------------
    // Benutzer deaktivieren
    // -------------------------------------------------

    @Transactional
    public OAuth2Benutzer deactivate(Integer id) {

        OAuth2Benutzer benutzer = getRequiredUser(id);

        benutzer.setEnabled(false);

        return repository.save(benutzer);
    }

    // -------------------------------------------------
    // Benutzerdaten aktualisieren
    // -------------------------------------------------

    @Transactional
    public OAuth2Benutzer update(
            Integer id,
            String username,
            String email,
            String firstName,
            String lastName) {

        OAuth2Benutzer benutzer = getRequiredUser(id);

        benutzer.setUsername(username);
        benutzer.setEmail(email);
        benutzer.setFirstName(firstName);
        benutzer.setLastName(lastName);

        return repository.save(benutzer);
    }

    // -------------------------------------------------
    // Benutzer löschen
    // -------------------------------------------------

    @Transactional
    public void delete(Integer id) {

        OAuth2Benutzer benutzer = getRequiredUser(id);

        repository.delete(benutzer);
    }

    // -------------------------------------------------
    // Interne Hilfsmethode
    // -------------------------------------------------

    private OAuth2Benutzer getRequiredUser(Integer id) {

        return repository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "OAuth2-Benutzer nicht gefunden: " + id
                    )
                );
    }
}
