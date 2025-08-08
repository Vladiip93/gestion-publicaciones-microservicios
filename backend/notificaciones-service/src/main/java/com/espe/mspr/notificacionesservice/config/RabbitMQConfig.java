package com.espe.mspr.notificaciones.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.publication:publication.events}")
    private String publicationExchangeName;

    @Value("${rabbitmq.exchange.auth:auth.events}")
    private String authExchangeName;

    // Exchanges (topic)
    @Bean
    public TopicExchange publicationExchange() {
        return new TopicExchange(publicationExchangeName, true, false);
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange(authExchangeName, true, false);
    }

    // Queues
    @Bean
    public Queue pubQueue() {
        return QueueBuilder.durable("notifications.publication").build();
    }

    @Bean
    public Queue authQueue() {
        return QueueBuilder.durable("notifications.auth").build();
    }

    // Routing keys de publicaciones
    public static final String RK_PUB_APPROVED  = "PublicationApproved";
    public static final String RK_PUB_PUBLISHED = "PublicationPublished";
    public static final String RK_PUB_CHANGES   = "PublicationChangesRequested";

    // Bindings publicaciones
    @Bean
    public Binding bPubApproved(Queue pubQueue, TopicExchange publicationExchange) {
        return BindingBuilder.bind(pubQueue).to(publicationExchange).with(RK_PUB_APPROVED);
    }

    @Bean
    public Binding bPubPublished(Queue pubQueue, TopicExchange publicationExchange) {
        return BindingBuilder.bind(pubQueue).to(publicationExchange).with(RK_PUB_PUBLISHED);
    }

    @Bean
    public Binding bPubChanges(Queue pubQueue, TopicExchange publicationExchange) {
        return BindingBuilder.bind(pubQueue).to(publicationExchange).with(RK_PUB_CHANGES);
    }

    // Routing keys de auth
    public static final String RK_AUTH_REGISTERED = "UserRegistered";
    public static final String RK_AUTH_LOGIN      = "UserLoggedIn";

    // Bindings auth
    @Bean
    public Binding bAuthRegistered(Queue authQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(authQueue).to(authExchange).with(RK_AUTH_REGISTERED);
    }

    @Bean
    public Binding bAuthLogin(Queue authQueue, TopicExchange authExchange) {
        return BindingBuilder.bind(authQueue).to(authExchange).with(RK_AUTH_LOGIN);
    }

    // Conversor JSON
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper om) {
        return new Jackson2JsonMessageConverter(om);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf, Jackson2JsonMessageConverter c) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(c);
        return t;
    }
}
