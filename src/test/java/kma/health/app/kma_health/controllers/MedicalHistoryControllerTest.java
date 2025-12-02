package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.ui.MedicalHistoryController;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.PatientHistoryAccessException;
import kma.health.app.kma_health.service.PatientService;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalHistoryControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private Model model;

    @InjectMocks
    private MedicalHistoryController controller;

    private UUID userId;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        patientId = UUID.randomUUID();
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
    void testGetMedicalHistory_PatientAccess_Success() {
        setSecurityContext("PATIENT");
        List<AppointmentFullViewDto> history = Collections.emptyList();

        when(patientService.getPatientMedicalHistory(eq(patientId), eq(patientId), eq(UserRole.PATIENT)))
                .thenReturn(history);

        String result = controller.getMedicalHistory(userId, patientId, model);

        assertEquals("medical_history", result);
        verify(model).addAttribute("closedAppointments", history);
        verify(model).addAttribute("patientId", patientId);
    }

    @Test
    void testGetMedicalHistory_DoctorAccess_Success() {
        setSecurityContext("DOCTOR");
        List<AppointmentFullViewDto> history = Collections.emptyList();

        when(patientService.getPatientMedicalHistory(eq(patientId), eq(userId), eq(UserRole.DOCTOR)))
                .thenReturn(history);

        String result = controller.getMedicalHistory(userId, patientId, model);

        assertEquals("medical_history", result);
        verify(model).addAttribute("closedAppointments", history);
    }

    @Test
    void testGetMedicalHistory_AccessDenied() {
        setSecurityContext("PATIENT");

        when(patientService.getPatientMedicalHistory(eq(patientId), eq(patientId), eq(UserRole.PATIENT)))
                .thenThrow(new PatientHistoryAccessException("Access denied"));

        String result = controller.getMedicalHistory(userId, patientId, model);

        assertEquals("error/access_denied", result);
        verify(model).addAttribute(eq("errorTitle"), any());
        verify(model).addAttribute(eq("errorMessage"), any());
    }
}
