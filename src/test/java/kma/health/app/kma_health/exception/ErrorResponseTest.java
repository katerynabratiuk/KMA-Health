package kma.health.app.kma_health.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorResponseTest {

    @Test
    void testErrorResponse_Constructor() {
        ErrorResponse errorResponse = new ErrorResponse(404, "Resource not found");

        assertEquals(404, errorResponse.getStatusCode());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testErrorResponse_NoArgsConstructor() {
        ErrorResponse errorResponse = new ErrorResponse();

        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void testErrorResponse_SettersAndGetters() {
        ErrorResponse errorResponse = new ErrorResponse();

        LocalDateTime timestamp = LocalDateTime.now();
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatusCode(500);
        errorResponse.setMessage("Something went wrong");

        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(500, errorResponse.getStatusCode());
        assertEquals("Something went wrong", errorResponse.getMessage());
    }

    @Test
    void testErrorResponse_TimestampIsSet() {
        ErrorResponse errorResponse = new ErrorResponse(400, "Bad request");

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);
        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertTrue(errorResponse.getTimestamp().isAfter(before));
        assertTrue(errorResponse.getTimestamp().isBefore(after));
    }
}

