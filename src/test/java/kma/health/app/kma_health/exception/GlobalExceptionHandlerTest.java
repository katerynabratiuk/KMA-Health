package kma.health.app.kma_health.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleExaminationNotFoundException() {
        ExaminationNotFoundException ex = new ExaminationNotFoundException("Examination not found");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Examination not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleAppointmentNotFoundException() {
        AppointmentNotFoundException ex = new AppointmentNotFoundException("Appointment not found");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Appointment not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleDoctorSpecializationAgeRestrictionException() {
        DoctorSpecializationAgeRestrictionException ex = 
            new DoctorSpecializationAgeRestrictionException("Age restriction violated");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Age restriction violated", response.getBody().getMessage());
    }

    @Test
    public void testHandleInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatusCode());
        assertEquals("Invalid credentials", response.getBody().getMessage());
    }

    @Test
    public void testHandleRoleNotFoundException() {
        RoleNotFoundException ex = new RoleNotFoundException("Role not found");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().getStatusCode());
        assertEquals("Role not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleFeedbackNotPermitted() {
        FeedbackNotPermitted ex = new FeedbackNotPermitted("Feedback not allowed");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatusCode());
        assertEquals("Feedback not allowed", response.getBody().getMessage());
    }

    @Test
    public void testHandleEntityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Entity not found");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatusCode());
        assertEquals("Entity not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleMissingOpenAppointmentException() {
        MissingOpenAppointmentException ex = new MissingOpenAppointmentException("No open appointment");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("No open appointment", response.getBody().getMessage());
    }

    @Test
    public void testHandleInvalidFamilyDoctorReferralMethodException() {
        InvalidFamilyDoctorReferralMethodException ex = 
            new InvalidFamilyDoctorReferralMethodException("Invalid referral method");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Invalid referral method", response.getBody().getMessage());
    }

    @Test
    public void testHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        
        ResponseEntity<ErrorResponse> response = handler.handle(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().getStatusCode());
        assertEquals("Access denied", response.getBody().getMessage());
    }
}

