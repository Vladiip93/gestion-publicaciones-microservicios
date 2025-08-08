package com.espe.mspr.authservice.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.Map;
import java.util.Collections;

public class PasswordGrantAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationManager authenticationManager;

    public PasswordGrantAuthenticationProvider(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Nuestro token con username/password
        PasswordGrantAuthenticationToken authRequest =
                (PasswordGrantAuthenticationToken) authentication;

        // Cliente OAuth2 ya autenticado en el contexto
        Authentication clientPrincipal =
                SecurityContextHolder.getContext().getAuthentication();
        if (clientPrincipal == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }

        // Autentica usuario final contra DB
        Authentication userAuth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                )
        );

        // Parametros extra a reenviar (opcional)
        Map<String, Object> additionalParameters = Collections.unmodifiableMap(
                Map.of(
                        OAuth2ParameterNames.USERNAME, authRequest.getUsername(),
                        OAuth2ParameterNames.PASSWORD, authRequest.getPassword()
                )
        );

        // Construye el token de autorización OAuth2 final
        return new PasswordGrantAuthenticationToken(
                clientPrincipal,
                authRequest.getUsername(),
                authRequest.getPassword(),
                additionalParameters
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PasswordGrantAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
