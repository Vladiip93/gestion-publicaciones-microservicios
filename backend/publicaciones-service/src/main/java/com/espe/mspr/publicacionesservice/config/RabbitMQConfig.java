package com.espe.mspr.publicacionesservice.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer; // <— ESTE es el bueno
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange publicationExchange(
            @Value("${rabbitmq.exchange.name:publication.events}") String exchangeName) {
        return new TopicExchange(exchangeName, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /** Personaliza el RabbitTemplate autoconfigurado por Spring Boot */
    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer(Jackson2JsonMessageConverter converter) {
        return template -> {
            template.setMessageConverter(converter);
            template.setMandatory(true);
            // Si quieres logs:
            // template.setReturnsCallback(r -> System.out.println("Returned: " + r));
            // template.setConfirmCallback((corr, ack, cause) ->
            //         System.out.println("Confirm ack=" + ack + " cause=" + cause));
        };
    }
}
