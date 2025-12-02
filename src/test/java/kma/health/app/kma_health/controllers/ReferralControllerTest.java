package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.dto.doctorDetail.DoctorDetailDto;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.DoctorSearchService;
import kma.health.app.kma_health.service.PatientService;
import kma.health.app.kma_health.service.ReferralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReferralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReferralService referralService;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private DoctorSearchService doctorSearchService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCanCreateReferralWithDoctorType() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        DoctorDetailDto doctor = new DoctorDetailDto();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        // The endpoint uses @AuthenticationPrincipal UUID userId which is null with @WithMockUser
        // So we mock getDoctorById to return doctor for any input including null
        when(doctorSearchService.getDoctorById(any())).thenReturn(doctor);
        when(patientService.getPatientById(any(UUID.class))).thenReturn(patient);

        mockMvc.perform(post("/api/referral")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"patientId\":\"" + patientId + "\",\"doctorTypeName\":\"Cardiologist\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCanCreateReferralWithExamination() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        DoctorDetailDto doctor = new DoctorDetailDto();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        when(doctorSearchService.getDoctorById(any())).thenReturn(doctor);
        when(patientService.getPatientById(any(UUID.class))).thenReturn(patient);

        mockMvc.perform(post("/api/referral")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"patientId\":\"" + patientId + "\",\"examinationId\":1}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testPatientCannotCreateReferral() throws Exception {
        mockMvc.perform(post("/api/referral")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"patientId\":\"" + UUID.randomUUID() + "\",\"doctorTypeName\":\"Cardiologist\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testAnonymousUserCannotCreateReferral() throws Exception {
        mockMvc.perform(post("/api/referral")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"patientId\":\"" + UUID.randomUUID() + "\",\"doctorTypeName\":\"Cardiologist\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCannotCreateReferralWhenNotFound() throws Exception {
        when(doctorSearchService.getDoctorById(any(UUID.class))).thenReturn(null);

        mockMvc.perform(post("/api/referral")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"patientId\":\"" + UUID.randomUUID() + "\",\"doctorTypeName\":\"Cardiologist\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "LAB_ASSISTANT")
    void testLabAssistantCannotCreateReferral() throws Exception {
        mockMvc.perform(post("/api/referral")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"patientId\":\"" + UUID.randomUUID() + "\",\"doctorTypeName\":\"Cardiologist\"}"))
                .andExpect(status().isForbidden());
    }
}

