
package de.ea234.securitydemo.security.oauth2.dto;

public record OAuth2BenutzerResponse(
        Integer id,
        String provider,
        String providerUserId,
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled
) {
}
