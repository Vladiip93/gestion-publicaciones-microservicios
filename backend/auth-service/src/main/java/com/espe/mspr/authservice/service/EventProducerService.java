package com.espe.mspr.authservice.service;

import com.espe.mspr.authservice.config.RabbitMQConfig;
import com.espe.mspr.authservice.events.AuditEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EventProducerService {

    private final RabbitTemplate rabbitTemplate;

    public EventProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserRegisteredEvent(String username) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AUDIT_EXCHANGE,
                RabbitMQConfig.AUDIT_ROUTING_KEY,
                new AuditEvent("REGISTER", username, Instant.now())
        );
    }

    public void sendUserLoginEvent(String username) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AUDIT_EXCHANGE,
                RabbitMQConfig.AUDIT_ROUTING_KEY,
                new AuditEvent("LOGIN", username, Instant.now())
        );
    }

    public void sendUserLogoutEvent(String username) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.AUDIT_EXCHANGE,
                RabbitMQConfig.AUDIT_ROUTING_KEY,
                new AuditEvent("LOGOUT", username, Instant.now())
        );
    }
}
