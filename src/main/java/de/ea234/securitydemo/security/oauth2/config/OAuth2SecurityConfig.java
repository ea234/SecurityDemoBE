package de.ea234.securitydemo.security.oauth2.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import de.ea234.securitydemo.security.oauth2.service.OAuth2UserService;

@Configuration
@Profile("jpa")
public class OAuth2SecurityConfig {

    private final OAuth2UserService oauth2UserService;

    public OAuth2SecurityConfig(OAuth2UserService oauth2UserService) {
        this.oauth2UserService = oauth2UserService;
    }

    public void configure(HttpSecurity http) throws Exception {

        http.oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .oidcUserService(oauth2UserService)
            )
        );
    }
}
