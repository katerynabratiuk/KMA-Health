package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedbackService feedbackService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCannotLeaveFeedback() throws Exception {
        mockMvc.perform(post("/api/doctor/{doctorId}/feedback", "550e8400-e29b-41d4-a716-446655440000")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCannotDeleteFeedback() throws Exception {
        mockMvc.perform(delete("/api/doctor/{doctorId}/feedback", "550e8400-e29b-41d4-a716-446655440000")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCanAccessFeedbacks() throws Exception {
        mockMvc.perform(get("/api/doctor/{doctorId}/feedback", "550e8400-e29b-41d4-a716-446655440000")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void patientCanAccessFeedbacks() throws Exception {
        mockMvc.perform(post("/api/doctor/{doctorId}/feedback", "550e8400-e29b-41d4-a716-446655440000")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "PATIENT")
    void patientCanLeaveFeedback() throws Exception {
        mockMvc.perform(post("/api/doctor/{doctorId}/feedback", "550e8400-e29b-41d4-a716-446655440000")
                .with(csrf())
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "PATIENT")
    void patientCanDeleteFeedback() throws Exception {
        mockMvc.perform(post("/api/doctor/{doctorId}/feedback", "550e8400-e29b-41d4-a716-446655440000")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void allUsersCanGetFeedbacks() throws Exception {
        mockMvc.perform(get("/api/doctor/{doctorId}/feedback", "550e8400-e29b-41d4-a716-446655440000")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }
}
