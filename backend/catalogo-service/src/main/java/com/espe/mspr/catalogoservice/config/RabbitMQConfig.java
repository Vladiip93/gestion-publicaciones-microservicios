package com.espe.mspr.catalogoservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /** Nombre del exchange donde Publicaciones publica sus eventos */
    @Bean
    public TopicExchange publicationExchange(
            @Value("${rabbitmq.exchange.name}") String exchangeName) {
        return ExchangeBuilder.topicExchange(exchangeName)
                .durable(true)
                .build();
    }

    /** Cola dedicada del catálogo */
    @Bean
    public Queue catalogQueue(@Value("${rabbitmq.queue.name}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    /** Enlazamos la cola con el exchange para el patrón de llave indicado */
    @Bean
    public Binding binding(Queue catalogQueue,
                           TopicExchange publicationExchange,
                           @Value("${rabbitmq.routing-key.pattern}") String routingKeyPattern) {
        return BindingBuilder.bind(catalogQueue)
                .to(publicationExchange)
                .with(routingKeyPattern);
    }
}
