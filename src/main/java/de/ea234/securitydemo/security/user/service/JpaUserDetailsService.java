
package de.ea234.securitydemo.security.user.service;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ea234.securitydemo.security.user.entity.Benutzer;
import de.ea234.securitydemo.security.user.repository.BenutzerRepository;

@Service
@Profile("jpa")
@Transactional(readOnly = true)
public class JpaUserDetailsService implements UserDetailsService {

    private final BenutzerRepository benutzerRepository;

    public JpaUserDetailsService(BenutzerRepository benutzerRepository) {
        this.benutzerRepository = benutzerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Benutzer benutzer =
            benutzerRepository.findByUsernameIgnoreCase(username);

        if (benutzer == null) {
            throw new UsernameNotFoundException(
                "Benutzer nicht gefunden: " + username
            );
        }

        return benutzer;
    }
}
