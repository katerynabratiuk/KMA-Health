package kma.health.app.kma_health.exception;

public class ExaminationNotFoundException extends RuntimeException {
    public ExaminationNotFoundException(String message) {
        super(message);
    }
}
