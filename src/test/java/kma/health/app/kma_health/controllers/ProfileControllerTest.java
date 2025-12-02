package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.dto.ProfileDto;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private JwtUtils jwtUtils;

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
    @WithAnonymousUser
    void testProfilePage_ShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/ui/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testProfilePage_PatientCanAccessProfile() throws Exception {
        ProfileDto profileDto = createPatientProfileDto("Test Patient", "patient@example.com");

        when(profileService.getProfileData(any(), anyString())).thenReturn(profileDto);

        mockMvc.perform(get("/ui/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("userRole", "PATIENT"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testProfilePage_DoctorCanAccessProfile() throws Exception {
        ProfileDto profileDto = createPatientProfileDto("Dr. Test", "doctor@example.com");

        when(profileService.getProfileData(any(), anyString())).thenReturn(profileDto);

        mockMvc.perform(get("/ui/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("userRole", "DOCTOR"));
    }

    @Test
    @WithMockUser(roles = "LAB_ASSISTANT")
    void testProfilePage_LabAssistantCanAccessProfile() throws Exception {
        ProfileDto profileDto = createPatientProfileDto("Lab Assistant", "lab@example.com");

        when(profileService.getProfileData(any(), anyString())).thenReturn(profileDto);

        mockMvc.perform(get("/ui/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("userRole", "LAB_ASSISTANT"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testProfilePage_ShouldIncludePlannedAppointments() throws Exception {
        ProfileDto profileDto = createPatientProfileDto("Test Patient", "patient@example.com");

        when(profileService.getProfileData(any(), anyString())).thenReturn(profileDto);

        mockMvc.perform(get("/ui/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("plannedAppointments"));
    }
}

