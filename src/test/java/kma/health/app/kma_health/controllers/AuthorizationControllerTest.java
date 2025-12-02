package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.RegistrationService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithAnonymousUser
    void testLogin_Success() throws Exception {
        when(authService.loginByEmail(anyString(), anyString(), any(UserRole.class)))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"identifier\":\"test@example.com\",\"password\":\"password123\",\"role\":\"PATIENT\",\"method\":\"EMAIL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andExpect(jsonPath("$.message").value("Successfully logged in"));
    }

    @Test
    @WithAnonymousUser
    void testLogin_WithEmailMethod() throws Exception {
        when(authService.loginByEmail(anyString(), anyString(), any(UserRole.class)))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"identifier\":\"test@example.com\",\"password\":\"password123\",\"role\":\"PATIENT\",\"method\":\"EMAIL\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testLogin_WithPhoneMethod() throws Exception {
        when(authService.loginByPhone(anyString(), anyString(), any(UserRole.class)))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"identifier\":\"+380991234567\",\"password\":\"password123\",\"role\":\"PATIENT\",\"method\":\"PHONE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testLogin_WithPassportMethod() throws Exception {
        when(authService.loginByPassport(anyString(), anyString(), any(UserRole.class)))
                .thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"identifier\":\"AB123456\",\"password\":\"password123\",\"role\":\"PATIENT\",\"method\":\"PASSPORT\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testRegister_Success() throws Exception {
        when(registrationService.register(any(kma.health.app.kma_health.dto.DoctorRegisterRequest.class)))
                .thenReturn("Patient registered successfully");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"new@example.com\",\"password\":\"password123\",\"role\":\"PATIENT\",\"fullName\":\"John Doe\",\"birthDate\":\"1990-01-01\",\"passportNumber\":\"AB123456\",\"phoneNumber\":\"+380991234567\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient registered successfully"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testUpdateProfile_Success() throws Exception {
        doNothing().when(authService).updateProfile(any(UUID.class), any());

        mockMvc.perform(patch("/api/auth/profile")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"updated@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated successfully"));
    }

    @Test
    @WithAnonymousUser
    void testUpdateProfile_Unauthorized() throws Exception {
        mockMvc.perform(patch("/api/auth/profile")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"email\":\"updated@example.com\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testDeleteProfile_Success() throws Exception {
        doNothing().when(authService).deleteProfile(any(UUID.class));

        mockMvc.perform(delete("/api/auth/profile")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile deleted successfully"));
    }

    @Test
    @WithAnonymousUser
    void testDeleteProfile_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/auth/profile")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCanUpdateProfile() throws Exception {
        doNothing().when(authService).updateProfile(any(UUID.class), any());

        mockMvc.perform(patch("/api/auth/profile")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"phoneNumber\":\"+380997654321\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "LAB_ASSISTANT")
    void testLabAssistantCanDeleteProfile() throws Exception {
        doNothing().when(authService).deleteProfile(any(UUID.class));

        mockMvc.perform(delete("/api/auth/profile")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}

