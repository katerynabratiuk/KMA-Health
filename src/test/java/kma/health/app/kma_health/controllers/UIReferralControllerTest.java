package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.ui.UIReferralController;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.ReferralService;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UIReferralControllerTest {

    @Mock
    private ReferralService referralService;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private Model model;

    @InjectMocks
    private UIReferralController controller;

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

    @Test
    void testGetReferralsPage_PatientAccessesOwn() throws AccessDeniedException {
        setSecurityContext(patientId, "PATIENT");
        when(referralService.getActiveReferrals(patientId)).thenReturn(Collections.emptyList());

        String result = controller.getReferralsPage(patientId, model);

        assertEquals("referrals", result);
        verify(model).addAttribute("referrals", Collections.emptyList());
        verify(model).addAttribute("userRole", "PATIENT");
        verify(model).addAttribute("patientId", patientId);
    }

    @Test
    void testGetReferralsPage_PatientAccessesOther_Denied() {
        UUID otherPatientId = UUID.randomUUID();
        setSecurityContext(patientId, "PATIENT");

        assertThrows(AccessDeniedException.class,
                () -> controller.getReferralsPage(otherPatientId, model));
    }

    @Test
    void testGetReferralsPage_DoctorWithOpenAppointment() throws AccessDeniedException {
        setSecurityContext(doctorId, "DOCTOR");
        when(appointmentService.haveOpenAppointment(doctorId, patientId)).thenReturn(true);
        when(referralService.getActiveReferrals(patientId)).thenReturn(Collections.emptyList());

        String result = controller.getReferralsPage(patientId, model);

        assertEquals("referrals", result);
        verify(model).addAttribute("userRole", "DOCTOR");
    }

    @Test
    void testGetReferralsPage_DoctorWithoutOpenAppointment_Denied() {
        setSecurityContext(doctorId, "DOCTOR");
        when(appointmentService.haveOpenAppointment(doctorId, patientId)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> controller.getReferralsPage(patientId, model));
    }

    @Test
    void testGetReferralsPage_AnonymousUser_Denied() {
        var auth = new UsernamePasswordAuthenticationToken(
                UUID.randomUUID(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OTHER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(AccessDeniedException.class,
                () -> controller.getReferralsPage(patientId, model));
    }

    @Test
    void testGetReferralsPage_WithReferrals() throws AccessDeniedException {
        setSecurityContext(patientId, "PATIENT");
        
        ReferralDto referralDto = new ReferralDto();
        List<ReferralDto> referrals = List.of(referralDto);
        
        when(referralService.getActiveReferrals(patientId)).thenReturn(referrals);

        String result = controller.getReferralsPage(patientId, model);

        assertEquals("referrals", result);
        verify(model).addAttribute("referrals", referrals);
    }
}
