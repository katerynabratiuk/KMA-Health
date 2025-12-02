package kma.health.app.kma_health.controllers;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.controller.ui.UIAppointmentController;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UIAppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private Model model;

    @InjectMocks
    private UIAppointmentController controller;

    private UUID userId;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testAppointment_NullUserId_RedirectsToLogin() {
        setSecurityContext("PATIENT");

        String result = controller.appointment(appointmentId, null, model);

        assertEquals("redirect:/ui/login", result);
    }

    @Test
    void testAppointment_PatientAccess_Success() throws Exception {
        setSecurityContext("PATIENT");
        AppointmentFullViewDto appointmentDto = new AppointmentFullViewDto();

        when(appointmentService.getFullAppointment(eq(appointmentId), eq(userId)))
                .thenReturn(appointmentDto);

        String result = controller.appointment(appointmentId, userId, model);

        assertEquals("appointment", result);
        verify(model).addAttribute("appointment", appointmentDto);
        verify(model).addAttribute("userRole", "PATIENT");
        verify(model).addAttribute("userId", userId);
    }

    @Test
    void testAppointment_DoctorAccess_Success() throws Exception {
        setSecurityContext("DOCTOR");
        AppointmentFullViewDto appointmentDto = new AppointmentFullViewDto();

        when(appointmentService.getFullAppointment(eq(appointmentId), eq(userId)))
                .thenReturn(appointmentDto);

        String result = controller.appointment(appointmentId, userId, model);

        assertEquals("appointment", result);
        verify(model).addAttribute("userRole", "DOCTOR");
    }

    @Test
    void testAppointment_AccessDenied() throws Exception {
        setSecurityContext("PATIENT");

        when(appointmentService.getFullAppointment(eq(appointmentId), eq(userId)))
                .thenThrow(new AccessDeniedException("Access denied"));

        String result = controller.appointment(appointmentId, userId, model);

        assertEquals("error/403", result);
    }

    @Test
    void testAppointment_NotFound_EntityNotFoundException() throws Exception {
        setSecurityContext("PATIENT");

        when(appointmentService.getFullAppointment(eq(appointmentId), eq(userId)))
                .thenThrow(new EntityNotFoundException("Appointment not found"));

        String result = controller.appointment(appointmentId, userId, model);

        assertEquals("error/404", result);
    }

    @Test
    void testAppointment_NotFound_AppointmentNotFoundException() throws Exception {
        setSecurityContext("PATIENT");

        when(appointmentService.getFullAppointment(eq(appointmentId), eq(userId)))
                .thenThrow(new AppointmentNotFoundException("Appointment not found"));

        String result = controller.appointment(appointmentId, userId, model);

        assertEquals("error/404", result);
    }
}
