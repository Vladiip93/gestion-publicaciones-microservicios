package com.espe.mspr.authservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String AUDIT_QUEUE       = "auth.audit";
    public static final String AUDIT_EXCHANGE    = "auth.audit.ex";
    public static final String AUDIT_ROUTING_KEY = "audit.key";

    @Bean
    public Queue auditQueue() {
        // durable true para que sobreviva reinicios
        return new Queue(AUDIT_QUEUE, true);
    }

    @Bean
    public DirectExchange auditExchange() {
        return new DirectExchange(AUDIT_EXCHANGE, true, false);
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, DirectExchange auditExchange) {
        return BindingBuilder
                .bind(auditQueue)
                .to(auditExchange)
                .with(AUDIT_ROUTING_KEY);
    }
}
