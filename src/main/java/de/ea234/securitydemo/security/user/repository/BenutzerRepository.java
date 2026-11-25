
package de.ea234.securitydemo.security.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.ea234.securitydemo.security.user.entity.Benutzer;

@Repository
public interface BenutzerRepository extends JpaRepository<Benutzer, Integer> {

    void deleteByUsername(String username);

    boolean existsByUsername(String username);

    Benutzer findByUsernameIgnoreCase(String username);
    
    Optional<Benutzer> findByUsername(String username);
}
