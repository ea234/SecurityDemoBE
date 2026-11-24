
package de.ea234.securitydemo.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestSecurityTestController {

    private static final DateTimeFormatter FORMATTER =  DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss:SSS");

    @GetMapping("/api/security/test/ping")
    public Map<String, Object> ping(Principal principal) {

        return createResponse(
            "Authentifizierung erfolgreich",
            principal.getName()
        );
    }

    @GetMapping("/api/security/test/admin")
    public Map<String, Object> admin(Principal principal) {

        return createResponse(
            "Admin-Zugriff erfolgreich",
            principal.getName()
        );
    }

    private Map<String, Object> createResponse(
            String message, String username) {

        long timestamp = System.currentTimeMillis();

        String datumString = LocalDateTime.now()
            .format(FORMATTER);

        return Map.of(
            "message", message,
            "username", username,
            "datumTimestamp", timestamp,
            "datumString", datumString
        );
    }
}
