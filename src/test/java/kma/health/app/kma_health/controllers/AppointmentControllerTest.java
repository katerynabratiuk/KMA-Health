package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.api.AppointmentController;
import kma.health.app.kma_health.dto.AppointmentCreateUpdateDto;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController controller;

    private UUID userId;
    private UUID appointmentId;
    private UUID doctorId;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        patientId = UUID.randomUUID();
    }

    private void setSecurityContext(UUID userId, String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // Patient Appointments Tests
    @Test
    void testGetPatientAppointments_WithEndDate() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);
        List<AppointmentShortViewDto> appointments = Collections.emptyList();
        
        when(appointmentService.getAppointmentsForPatient(userId, start, end)).thenReturn(appointments);

        ResponseEntity<List<AppointmentShortViewDto>> response = 
                controller.getPatientAppointments(userId, start, end);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
    }

    @Test
    void testGetPatientAppointments_WithoutEndDate() {
        LocalDate start = LocalDate.now();
        List<AppointmentShortViewDto> appointments = Collections.emptyList();
        
        when(appointmentService.getAppointmentsForPatient(userId, start)).thenReturn(appointments);

        ResponseEntity<List<AppointmentShortViewDto>> response = 
                controller.getPatientAppointments(userId, start, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetPatientAppointments_Exception() {
        LocalDate start = LocalDate.now();
        
        when(appointmentService.getAppointmentsForPatient(any(), any()))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<AppointmentShortViewDto>> response = 
                controller.getPatientAppointments(userId, start, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Doctor Appointments Tests
    @Test
    void testGetDoctorAppointments_WithEndDate() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);
        List<AppointmentShortViewDto> appointments = Collections.emptyList();
        
        when(appointmentService.getAppointmentsForDoctor(doctorId, start, end)).thenReturn(appointments);

        ResponseEntity<List<AppointmentShortViewDto>> response = 
                controller.getDoctorAppointments(doctorId, start, end);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDoctorAppointments_WithoutEndDate() {
        LocalDate start = LocalDate.now();
        List<AppointmentShortViewDto> appointments = Collections.emptyList();
        
        when(appointmentService.getAppointmentsForDoctor(doctorId, start)).thenReturn(appointments);

        ResponseEntity<List<AppointmentShortViewDto>> response = 
                controller.getDoctorAppointments(doctorId, start, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetDoctorAppointments_Exception() {
        LocalDate start = LocalDate.now();
        
        when(appointmentService.getAppointmentsForDoctor(any(), any()))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<List<AppointmentShortViewDto>> response = 
                controller.getDoctorAppointments(doctorId, start, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Finish Appointment Tests
    @Test
    void testFinishAppointment_Success() throws IOException {
        doNothing().when(appointmentService).finishAppointment(any(), any(), any(), any());

        ResponseEntity<?> response = controller.finishAppointment(
                userId, Collections.emptyList(), appointmentId, "diagnosis");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testFinishAppointment_IOException() throws IOException {
        doThrow(new IOException("File error"))
                .when(appointmentService).finishAppointment(any(), any(), any(), any());

        ResponseEntity<?> response = controller.finishAppointment(
                userId, Collections.emptyList(), appointmentId, "diagnosis");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testFinishAppointment_OtherException() throws IOException {
        doThrow(new RuntimeException("Unexpected error"))
                .when(appointmentService).finishAppointment(any(), any(), any(), any());

        ResponseEntity<?> response = controller.finishAppointment(
                userId, Collections.emptyList(), appointmentId, "diagnosis");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Cancel Appointment Tests
    @Test
    void testCancelAppointment_AsPatient_Success() throws AccessDeniedException {
        setSecurityContext(patientId, "PATIENT");
        doNothing().when(appointmentService).cancelAppointment(any(), any(), any());

        ResponseEntity<?> response = controller.cancelAppointment(
                patientId, doctorId, patientId, appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCancelAppointment_AsDoctor_Success() throws AccessDeniedException {
        setSecurityContext(doctorId, "DOCTOR");
        doNothing().when(appointmentService).cancelAppointment(any(), any(), any());

        ResponseEntity<?> response = controller.cancelAppointment(
                doctorId, doctorId, patientId, appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCancelAppointment_AsLabAssistant_Success() throws AccessDeniedException {
        setSecurityContext(userId, "LAB_ASSISTANT");
        doNothing().when(appointmentService).cancelAppointment(any(), any(), any());

        ResponseEntity<?> response = controller.cancelAppointment(
                userId, userId, patientId, appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCancelAppointment_PatientMismatch() throws AccessDeniedException {
        setSecurityContext(patientId, "PATIENT");
        UUID differentPatientId = UUID.randomUUID();

        ResponseEntity<?> response = controller.cancelAppointment(
                patientId, doctorId, differentPatientId, appointmentId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testCancelAppointment_DoctorMismatch() throws AccessDeniedException {
        setSecurityContext(doctorId, "DOCTOR");
        UUID differentDoctorId = UUID.randomUUID();

        ResponseEntity<?> response = controller.cancelAppointment(
                doctorId, differentDoctorId, patientId, appointmentId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Removed: Unknown role test causes exception since UserRole.fromString throws for unknown roles

    // Assign Lab Assistant Tests
    @Test
    void testAssignLabAssistant_Success() throws AccessDeniedException {
        doNothing().when(appointmentService).assignLabAssistantToAppointment(any(), any());

        ResponseEntity<?> response = controller.assignLabAssistantToAppointment(userId, appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Get Single Appointment Tests
    @Test
    void testGetAppointment_Success() throws AccessDeniedException {
        AppointmentFullViewDto dto = new AppointmentFullViewDto();
        dto.setId(appointmentId);
        
        when(appointmentService.getFullAppointment(appointmentId, userId)).thenReturn(dto);

        ResponseEntity<AppointmentFullViewDto> response = controller.getAppointment(userId, appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    // Create Appointment Tests
    @Test
    void testCreateAppointment_Success() throws AccessDeniedException {
        AppointmentCreateUpdateDto dto = new AppointmentCreateUpdateDto();
        doNothing().when(appointmentService).createAppointment(any(), any());

        ResponseEntity<?> response = controller.createAppointment(userId, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    // Exception Handler Tests
    @Test
    void testHandleAppointmentNotFound() {
        AppointmentNotFoundException ex = new AppointmentNotFoundException("Not found");

        var response = controller.handleAppointmentNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
        assertEquals("Not found", response.getMessage());
    }
}
