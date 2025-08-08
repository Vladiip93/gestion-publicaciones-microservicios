// src/main/java/com/espe/mspr/authservice/listener/AuthenticationSuccessEventListener.java
package com.espe.mspr.authservice.listener;

import com.espe.mspr.authservice.service.EventProducerService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

@Component
public class AuthenticationSuccessEventListener
        implements ApplicationListener<AuthenticationSuccessEvent> {

    private final EventProducerService producer;

    public AuthenticationSuccessEventListener(EventProducerService producer) {
        this.producer = producer;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        // Sólo nos interesan los logins de usuario/password (no otros tipos de auth)
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            String username = auth.getName();
            producer.sendUserLoginEvent(username);
        }
    }
}
