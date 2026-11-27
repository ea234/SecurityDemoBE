
package de.ea234.securitydemo.security.oauth2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OAuth2BenutzerUpdateRequest(

        @NotBlank
        String username,

        @Email
        String email,

        String firstName,

        String lastName
) {
}
