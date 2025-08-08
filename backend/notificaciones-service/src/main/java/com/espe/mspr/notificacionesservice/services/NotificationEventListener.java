// Archivo: services/NotificationEventListener.java
package com.espe.mspr.notificacionesservice.services;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventListener {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleAllEvents(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String payload = new String(message.getBody());

        System.out.println("================= NUEVA NOTIFICACIÓN =================");
        System.out.println("Evento Recibido: " + routingKey);
        System.out.println("Datos: " + payload);
        System.out.println("Acción: Enviar email, notificación push, etc.");
        System.out.println("======================================================");
    }
}