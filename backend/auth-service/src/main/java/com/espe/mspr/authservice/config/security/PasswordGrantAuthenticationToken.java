package com.espe.mspr.authservice.config.security;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Objects;

public class PasswordGrantAuthenticationToken
        extends OAuth2AuthorizationGrantAuthenticationToken {

    private final String username;
    private final String password;

    /**
     * Constructor usado por el converter (antes de autenticar al usuario).
     */
    public PasswordGrantAuthenticationToken(String username, String password) {
        super(AuthorizationGrantType.PASSWORD, null, null);
        this.username = Objects.requireNonNull(username, "username cannot be null");
        this.password = Objects.requireNonNull(password, "password cannot be null");
    }

    /**
     * Constructor usado tras autenticar al usuario: incluye clientPrincipal y params.
     */
    public PasswordGrantAuthenticationToken(
            Authentication clientPrincipal,
            String username,
            String password,
            Map<String, Object> additionalParameters) {

        super(AuthorizationGrantType.PASSWORD, clientPrincipal, additionalParameters);
        this.username = Objects.requireNonNull(username, "username cannot be null");
        this.password = Objects.requireNonNull(password, "password cannot be null");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
