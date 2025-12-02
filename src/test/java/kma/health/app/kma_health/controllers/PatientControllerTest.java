package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.api.PatientController;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.PatientContactsDto;
import kma.health.app.kma_health.dto.PatientDto;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.PatientService;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private PatientController controller;

    private UUID patientId;
    private UUID doctorId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
    }

    private void setSecurityContext(UUID userId, String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // Profile Tests
    @Test
    void testGetProfile_Success() {
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setFullName("Test Patient");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        
        when(patientService.getPatientById(patientId)).thenReturn(patient);

        PatientDto result = controller.getProfile(patientId);

        assertNotNull(result);
        assertEquals("Test Patient", result.getFullName());
    }

    // Patient Contacts Tests
    @Test
    void testGetPatientContacts_Success() {
        PatientContactsDto contacts = new PatientContactsDto();
        when(patientService.getPatientContacts(patientId)).thenReturn(contacts);

        ResponseEntity<PatientContactsDto> response = controller.getPatientContacts(patientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contacts, response.getBody());
    }

    @Test
    void testGetPatientContacts_NotFound() {
        when(patientService.getPatientContacts(patientId)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<PatientContactsDto> response = controller.getPatientContacts(patientId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Patient History Tests
    @Test
    void testGetPatientHistory_AsPatient_OwnHistory() {
        setSecurityContext(patientId, "PATIENT");
        
        List<AppointmentFullViewDto> history = Collections.emptyList();
        when(patientService.getPatientMedicalHistory(eq(patientId), any(), eq(UserRole.PATIENT)))
                .thenReturn(history);

        ResponseEntity<List<AppointmentFullViewDto>> response = 
                controller.getPatientHistory(patientId, patientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(history, response.getBody());
    }

    @Test
    void testGetPatientHistory_AsPatient_OtherPatient_Forbidden() {
        setSecurityContext(patientId, "PATIENT");
        UUID otherPatientId = UUID.randomUUID();

        ResponseEntity<List<AppointmentFullViewDto>> response = 
                controller.getPatientHistory(patientId, otherPatientId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetPatientHistory_AsDoctor() {
        setSecurityContext(doctorId, "DOCTOR");
        
        List<AppointmentFullViewDto> history = Collections.emptyList();
        when(patientService.getPatientMedicalHistory(eq(patientId), eq(doctorId), eq(UserRole.DOCTOR)))
                .thenReturn(history);

        ResponseEntity<List<AppointmentFullViewDto>> response = 
                controller.getPatientHistory(doctorId, patientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetPatientHistory_OtherRole_Forbidden() {
        setSecurityContext(patientId, "LAB_ASSISTANT");

        ResponseEntity<List<AppointmentFullViewDto>> response = 
                controller.getPatientHistory(patientId, patientId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Patient Referrals Tests
    @Test
    void testGetReferrals_AsPatient_OwnReferrals() {
        setSecurityContext(patientId, "PATIENT");
        
        List<ReferralDto> referrals = Collections.emptyList();
        when(patientService.getPatientReferrals(eq(patientId), any(), eq(UserRole.PATIENT)))
                .thenReturn(referrals);

        ResponseEntity<List<ReferralDto>> response = 
                controller.getReferrals(patientId, patientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(referrals, response.getBody());
    }

    @Test
    void testGetReferrals_AsPatient_OtherPatient_Forbidden() {
        setSecurityContext(patientId, "PATIENT");
        UUID otherPatientId = UUID.randomUUID();

        ResponseEntity<List<ReferralDto>> response = 
                controller.getReferrals(patientId, otherPatientId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetReferrals_AsDoctor() {
        setSecurityContext(doctorId, "DOCTOR");
        
        List<ReferralDto> referrals = Collections.emptyList();
        when(patientService.getPatientReferrals(eq(patientId), eq(doctorId), eq(UserRole.DOCTOR)))
                .thenReturn(referrals);

        ResponseEntity<List<ReferralDto>> response = 
                controller.getReferrals(doctorId, patientId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetReferrals_OtherRole_Forbidden() {
        setSecurityContext(patientId, "LAB_ASSISTANT");

        ResponseEntity<List<ReferralDto>> response = 
                controller.getReferrals(patientId, patientId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
