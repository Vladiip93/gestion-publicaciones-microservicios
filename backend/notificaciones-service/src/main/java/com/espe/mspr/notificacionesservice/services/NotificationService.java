package com.espe.mspr.notificacionesservice.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // NOTA: usamos ObjectProvider para no requerir el bean sí o sí
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${notifications.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${notifications.mail.from:no-reply@mspr.local}")
    private String from;

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public void pushToClients(Object payload) {
        for (SseEmitter em : emitters) {
            try {
                em.send(SseEmitter.event().name("notification").data(payload));
            } catch (Exception ex) {
                emitters.remove(em);
            }
        }
    }

    public void sendEmail(String to, String subject, String htmlBody) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (!mailEnabled || mailSender == null) {
            log.info("[MAIL SKIPPED] enabled={}, senderPresent={}, to={}, subject={}",
                    mailEnabled, mailSender != null, to, subject);
            return;
        }
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(msg, StandardCharsets.UTF_8.name());
            h.setFrom(from);
            h.setTo(to);
            h.setSubject(subject);
            h.setText(htmlBody, true);
            mailSender.send(msg);
        } catch (Exception e) {
            log.error("Error enviando email", e);
        }
    }

    public Map<String, Object> buildToast(String type, String title, String message) {
        return Map.of("type", type, "title", title, "message", message, "ts", Instant.now().toString());
    }
}
