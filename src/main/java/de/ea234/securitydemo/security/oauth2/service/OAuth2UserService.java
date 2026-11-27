package de.ea234.securitydemo.security.oauth2.service;

import java.util.Locale;

import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.ea234.securitydemo.security.oauth2.entity.OAuth2Benutzer;
import de.ea234.securitydemo.security.oauth2.repository.OAuth2BenutzerRepository;

@Service
@Profile ( "jpa" )
public class OAuth2UserService implements org.springframework.security.oauth2.client.userinfo.OAuth2UserService< OidcUserRequest, OidcUser >
{

  private final OAuth2BenutzerRepository repository;

  private final OidcUserService          delegate = new OidcUserService();

  public OAuth2UserService( OAuth2BenutzerRepository repository )
  {
    this.repository = repository;
  }

  @Override
  @Transactional
  public OidcUser loadUser( OidcUserRequest userRequest ) throws OAuth2AuthenticationException
  {

    // Originalen OIDC-Benutzer laden
    OidcUser oidcUser = delegate.loadUser( userRequest );

    // Identität ermitteln
    String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase( Locale.ROOT );

    String providerUserId = oidcUser.getSubject();

    // Benutzerdaten aus den OIDC-Claims lesen
    String username = oidcUser.getClaimAsString( "preferred_username" );
    String email = oidcUser.getClaimAsString( "email" );
    String firstName = oidcUser.getClaimAsString( "given_name" );
    String lastName = oidcUser.getClaimAsString( "family_name" );

    if ( username == null || username.isBlank() )
    {
      username = providerUserId;
    }

    // Vorhandenen Benutzer suchen
    var optionalBenutzer = repository.findByProviderAndProviderUserId( provider, providerUserId );

    // Deaktivierte Benutzer zurückweisen
    if ( optionalBenutzer.isPresent() && !optionalBenutzer.get().isEnabled() )
    {

      throw new OAuth2AuthenticationException( new OAuth2Error( "access_denied", "Dieser Benutzer ist deaktiviert.", null ) );
    }

    // Vorhandenen Benutzer verwenden oder neuen anlegen
    OAuth2Benutzer benutzer = optionalBenutzer.orElseGet( OAuth2Benutzer::new );

    // Benutzerdaten aktualisieren
    benutzer.setProvider( provider );
    benutzer.setProviderUserId( providerUserId );
    benutzer.setUsername( username );
    benutzer.setEmail( email );
    benutzer.setFirstName( firstName );
    benutzer.setLastName( lastName );

    // Benutzer speichern
    repository.save( benutzer );

    // Originalen OIDC-Benutzer zurückgeben
    return oidcUser;
  }
}
