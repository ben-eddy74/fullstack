package com.app.health.entity;

import java.time.Instant;

public record PingResponse(String status, Instant timestamp) {
    public PingResponse {
        if (status == null || status.isBlank()) {
            status = "ok";
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }
}
