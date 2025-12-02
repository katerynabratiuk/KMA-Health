package kma.health.app.kma_health.exception;

public class AppointmentIsNotLinkedToReferralException extends RuntimeException {
    public AppointmentIsNotLinkedToReferralException(String message) {
        super(message);
    }
}
