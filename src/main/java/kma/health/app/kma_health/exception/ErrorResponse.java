package kma.health.app.kma_health.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(int statusCode, String message)
    {
        this.message = message;
        this.statusCode = statusCode;
    }
}
