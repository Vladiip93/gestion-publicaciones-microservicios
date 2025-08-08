package com.espe.mspr.authservice.events;

import java.time.Instant;

public record AuditEvent(
        String type,       // "LOGIN", "LOGOUT", "REGISTER"
        String username,
        Instant timestamp
) {}
