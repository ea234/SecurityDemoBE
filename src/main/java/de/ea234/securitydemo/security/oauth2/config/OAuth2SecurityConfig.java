package de.ea234.securitydemo.security.oauth2.config;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

/**
 * Konfiguration der OAuth2-Authentifizierung.
 *
 * Die eigentliche SecurityFilterChain wird durch
 * JpaSecurityConfig bereitgestellt.
 */
@Component
public class OAuth2SecurityConfig {

    public void configure(HttpSecurity http) throws Exception {

        http.oauth2Login(Customizer.withDefaults());

    }
}
