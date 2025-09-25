package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String login,       // email / phone / passport
        @NotBlank String password,
        @NotBlank String role         // "patient" or "doctor"
) {}
