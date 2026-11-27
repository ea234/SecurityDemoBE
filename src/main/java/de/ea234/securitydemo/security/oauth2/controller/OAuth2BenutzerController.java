
package de.ea234.securitydemo.security.oauth2.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import de.ea234.securitydemo.security.oauth2.dto.OAuth2BenutzerResponse;
import de.ea234.securitydemo.security.oauth2.dto.OAuth2BenutzerUpdateRequest;
import de.ea234.securitydemo.security.oauth2.entity.OAuth2Benutzer;
import de.ea234.securitydemo.security.oauth2.service.OAuth2BenutzerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/oauth2/users")
@Profile("jpa")
public class OAuth2BenutzerController {

    private final OAuth2BenutzerService service;

    public OAuth2BenutzerController(
            OAuth2BenutzerService service) {

        this.service = service;
    }

    @GetMapping
    public List<OAuth2BenutzerResponse> findAll() {

        return service.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public OAuth2BenutzerResponse findById(
            @PathVariable Integer id) {

        return toResponse(
                service.findById(id)
                    .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "OAuth2-Benutzer nicht gefunden"
                        )
                    )
        );
    }

    @PutMapping("/{id}")
    public OAuth2BenutzerResponse update(
            @PathVariable Integer id,
            @Valid @RequestBody OAuth2BenutzerUpdateRequest request) {

        return toResponse(
                service.update(
                    id,
                    request.username(),
                    request.email(),
                    request.firstName(),
                    request.lastName()
                )
        );
    }

    @PatchMapping("/{id}/activate")
    public OAuth2BenutzerResponse activate(
            @PathVariable Integer id) {

        return toResponse(service.activate(id));
    }

    @PatchMapping("/{id}/deactivate")
    public OAuth2BenutzerResponse deactivate(
            @PathVariable Integer id) {

        return toResponse(service.deactivate(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {

        service.delete(id);
    }

    private OAuth2BenutzerResponse toResponse(
            OAuth2Benutzer benutzer) {

        return new OAuth2BenutzerResponse(
                benutzer.getId(),
                benutzer.getProvider(),
                benutzer.getProviderUserId(),
                benutzer.getUsername(),
                benutzer.getEmail(),
                benutzer.getFirstName(),
                benutzer.getLastName(),
                benutzer.isEnabled()
        );
    }
}
