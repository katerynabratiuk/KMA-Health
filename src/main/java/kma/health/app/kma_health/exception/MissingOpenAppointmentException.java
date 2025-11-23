package kma.health.app.kma_health.exception;

public class MissingOpenAppointmentException extends RuntimeException {
    public MissingOpenAppointmentException(String message) {
        super(message);
    }
}
