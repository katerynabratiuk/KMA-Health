package kma.health.app.kma_health.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentStatusExceptionTest {

    @Test
    void testConstructor() {
        String message = "Invalid appointment status";
        AppointmentStatusException exception = new AppointmentStatusException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        AppointmentStatusException exception = new AppointmentStatusException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}

