package com.espe.mspr.publicacionesservice.services;

import com.espe.mspr.publicacionesservice.outbox.EventStatus;
import com.espe.mspr.publicacionesservice.outbox.OutboxEvent;
import com.espe.mspr.publicacionesservice.repositories.OutboxEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public OutboxEventPublisher(OutboxEventRepository outboxEventRepository, RabbitTemplate rabbitTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 10_000)
    @Transactional
    public void processOutboxEvents() {
        // usa el batch de 100 más antiguos
        List<OutboxEvent> events =
                outboxEventRepository.findTop50ByStatusOrderByFechaCreacionAsc(EventStatus.PENDIENTE);

        for (OutboxEvent event : events) {
            try {
                rabbitTemplate.convertAndSend(exchangeName, event.getEventType(), event.getPayload());
                event.setStatus(EventStatus.ENVIADO);
            } catch (Exception e) {
                System.err.println("Error al publicar evento " + event.getId() + ": " + e.getMessage());
                event.setStatus(EventStatus.ERROR);
            }
            outboxEventRepository.save(event);
        }
    }
}