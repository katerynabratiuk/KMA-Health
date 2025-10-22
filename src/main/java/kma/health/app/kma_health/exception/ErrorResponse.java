package kma.health.app.kma_health.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;
    private LocalDate timestamp;

    public ErrorResponse(int statusCode, String message)
    {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDate.now();
    }
}
