// OutboxDispatcher.java
package com.espe.mspr.publicacionesservice.outbox;

import com.espe.mspr.publicacionesservice.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxDispatcher {

    private final OutboxEventRepository repo;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name:mspr.exchange}")
    private String exchange;

    // Usa el mismo routing key que consumes en el catálogo
    @Value("${rabbitmq.routing-key.pattern:catalog.publications}")
    private String routingKey;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void pump() {
        List<OutboxEvent> pendings =
                repo.findTop50ByStatusOrderByFechaCreacionAsc(EventStatus.PENDIENTE);

        for (OutboxEvent e : pendings) {
            try {
                // Mandamos el payload tal cual (JSON) al catálogo
                rabbitTemplate.convertAndSend(exchange, routingKey, e.getPayload());
                e.setStatus(EventStatus.ENVIADO);
                repo.save(e);
                log.info("Outbox -> enviado id={} tipo={}", e.getId(), e.getEventType());
            } catch (Exception ex) {
                log.error("Outbox -> error enviando id={} : {}", e.getId(), ex.getMessage());
                e.setStatus(EventStatus.ERROR);

                repo.save(e);
            }
        }
    }
}
