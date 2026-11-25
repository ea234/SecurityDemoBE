
package de.ea234.securitydemo.security.jpa.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import de.ea234.securitydemo.security.user.entity.Benutzer;
import de.ea234.securitydemo.security.user.repository.BenutzerRepository;

@Component
@Profile("jpa")
public class JpaUserDataInitializer implements CommandLineRunner {

    private final BenutzerRepository benutzerRepository;
    private final PasswordEncoder passwordEncoder;

    public JpaUserDataInitializer(
            BenutzerRepository benutzerRepository,
            PasswordEncoder passwordEncoder) {

        this.benutzerRepository = benutzerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Benutzer admin = benutzerRepository
                .findByUsername("admin")
                .orElseGet(() -> new Benutzer(
                        "admin",
                        passwordEncoder.encode("admin"),
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ));

        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEnable(true);
        admin.setLocked(false);

        benutzerRepository.save(admin);

        Benutzer demo = benutzerRepository
                .findByUsername("demo")
                .orElseGet(() -> new Benutzer(
                        "demo",
                        passwordEncoder.encode("demo"),
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ));

        demo.setPassword(passwordEncoder.encode("demo"));
        demo.setEnable(true);
        demo.setLocked(false);

        benutzerRepository.save(demo);
    }
}
