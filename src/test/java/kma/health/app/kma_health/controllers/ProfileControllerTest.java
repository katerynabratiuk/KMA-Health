package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.ui.ProfileController;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.dto.ProfileDto;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.ProfileService;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private Model model;

    @InjectMocks
    private ProfileController controller;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
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

    private ProfileDto createPatientProfileDto(String fullName, String email) {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName(fullName);
        patient.setEmail(email);
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        ProfileDto dto = new ProfileDto(patient);
        dto.setPlannedAppointments(Collections.emptyList());
        return dto;
    }

    @Test
    void testProfilePage_PatientCanAccessProfile() {
        setSecurityContext("PATIENT");
        ProfileDto profileDto = createPatientProfileDto("Test Patient", "patient@example.com");

        when(profileService.getProfileData(any(), eq("PATIENT"))).thenReturn(profileDto);

        String result = controller.getProfilePage(userId, model);

        assertEquals("profile", result);
        verify(model).addAttribute("user", profileDto);
        verify(model).addAttribute("userRole", "PATIENT");
    }

    @Test
    void testProfilePage_DoctorCanAccessProfile() {
        setSecurityContext("DOCTOR");
        ProfileDto profileDto = createPatientProfileDto("Dr. Test", "doctor@example.com");

        when(profileService.getProfileData(any(), eq("DOCTOR"))).thenReturn(profileDto);

        String result = controller.getProfilePage(userId, model);

        assertEquals("profile", result);
        verify(model).addAttribute("userRole", "DOCTOR");
    }

    @Test
    void testProfilePage_LabAssistantCanAccessProfile() {
        setSecurityContext("LAB_ASSISTANT");
        ProfileDto profileDto = createPatientProfileDto("Lab Assistant", "lab@example.com");

        when(profileService.getProfileData(any(), eq("LAB_ASSISTANT"))).thenReturn(profileDto);

        String result = controller.getProfilePage(userId, model);

        assertEquals("profile", result);
        verify(model).addAttribute("userRole", "LAB_ASSISTANT");
    }

    @Test
    void testProfilePage_ShouldIncludePlannedAppointments() {
        setSecurityContext("PATIENT");
        ProfileDto profileDto = createPatientProfileDto("Test Patient", "patient@example.com");

        when(profileService.getProfileData(any(), eq("PATIENT"))).thenReturn(profileDto);

        String result = controller.getProfilePage(userId, model);

        assertEquals("profile", result);
        verify(model).addAttribute(eq("plannedAppointments"), any());
    }

    @Test
    void testCalendar_WithoutParams_UsesCurrentMonth() {
        setSecurityContext("PATIENT");
        List<AppointmentShortViewDto> appointments = Collections.emptyList();
        YearMonth currentYearMonth = YearMonth.now();

        when(appointmentService.getAppointmentsForDoctor(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(appointments);

        String result = controller.getCalendar(userId, null, null, model);

        assertEquals("doctor-calendar", result);
        verify(model).addAttribute("appointments", appointments);
        verify(model).addAttribute("currentYear", currentYearMonth.getYear());
        verify(model).addAttribute("currentMonth", currentYearMonth.getMonthValue());
    }

    @Test
    void testCalendar_WithYearAndMonthParams() {
        setSecurityContext("PATIENT");
        List<AppointmentShortViewDto> appointments = Collections.emptyList();

        when(appointmentService.getAppointmentsForDoctor(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(appointments);

        String result = controller.getCalendar(userId, 2024, 6, model);

        assertEquals("doctor-calendar", result);
        verify(model).addAttribute("currentYear", 2024);
        verify(model).addAttribute("currentMonth", 6);
    }

    @Test
    void testCalendar_WithOnlyYearParam_UsesCurrentMonth() {
        setSecurityContext("PATIENT");
        List<AppointmentShortViewDto> appointments = Collections.emptyList();
        YearMonth currentYearMonth = YearMonth.now();

        when(appointmentService.getAppointmentsForDoctor(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(appointments);

        String result = controller.getCalendar(userId, 2024, null, model);

        assertEquals("doctor-calendar", result);
        verify(model).addAttribute("currentYear", currentYearMonth.getYear());
        verify(model).addAttribute("currentMonth", currentYearMonth.getMonthValue());
    }

    @Test
    void testCalendar_WithOnlyMonthParam_UsesCurrentYear() {
        setSecurityContext("PATIENT");
        List<AppointmentShortViewDto> appointments = Collections.emptyList();
        YearMonth currentYearMonth = YearMonth.now();

        when(appointmentService.getAppointmentsForDoctor(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(appointments);

        String result = controller.getCalendar(userId, null, 3, model);

        assertEquals("doctor-calendar", result);
        verify(model).addAttribute("currentYear", currentYearMonth.getYear());
        verify(model).addAttribute("currentMonth", currentYearMonth.getMonthValue());
    }

    @Test
    void testCalendar_DoctorAccess() {
        setSecurityContext("DOCTOR");
        List<AppointmentShortViewDto> appointments = Collections.emptyList();

        when(appointmentService.getAppointmentsForDoctor(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(appointments);

        String result = controller.getCalendar(userId, 2025, 1, model);

        assertEquals("doctor-calendar", result);
    }
}
