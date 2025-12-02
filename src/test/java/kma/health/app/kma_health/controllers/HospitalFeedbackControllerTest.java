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

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HospitalFeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedbackService feedbackService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithAnonymousUser
    void testGetHospitalFeedback_Anonymous() throws Exception {
        when(feedbackService.getHospitalFeedbacks(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/hospitals/1/feedback"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testCreateHospitalFeedback_Success() throws Exception {
        mockMvc.perform(post("/api/hospitals/1/feedback")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"score\":5,\"comment\":\"Great hospital\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testDeleteHospitalFeedback_Success() throws Exception {
        mockMvc.perform(delete("/api/hospitals/1/feedback/1")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testCreateHospitalFeedback_Forbidden() throws Exception {
        mockMvc.perform(post("/api/hospitals/1/feedback")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"score\":5,\"comment\":\"Great hospital\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testCreateHospitalFeedback_Unauthorized() throws Exception {
        mockMvc.perform(post("/api/hospitals/1/feedback")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"score\":5,\"comment\":\"Great hospital\"}"))
                .andExpect(status().isForbidden());
    }
}

