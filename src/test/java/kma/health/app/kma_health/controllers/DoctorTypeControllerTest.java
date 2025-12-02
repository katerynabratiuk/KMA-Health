package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.DoctorTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DoctorTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorTypeService doctorTypeService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateDoctorType_Success() throws Exception {
        doNothing().when(doctorTypeService).createDoctorType(any());

        mockMvc.perform(post("/api/doctortype/")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"typeName\":\"Cardiologist\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateDoctorType_Conflict() throws Exception {
        doThrow(new DataIntegrityViolationException("Already exists"))
                .when(doctorTypeService).createDoctorType(any());

        mockMvc.perform(post("/api/doctortype/")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"typeName\":\"Cardiologist\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithAnonymousUser
    void testCreateDoctorType_AnonymousDenied() throws Exception {
        // The endpoint requires authentication - anonymous users are denied
        mockMvc.perform(post("/api/doctortype/")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"typeName\":\"Cardiologist\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testPatientCanCreateDoctorType() throws Exception {
        doNothing().when(doctorTypeService).createDoctorType(any());

        mockMvc.perform(post("/api/doctortype/")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"typeName\":\"Neurologist\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testDoctorCanCreateDoctorType() throws Exception {
        doNothing().when(doctorTypeService).createDoctorType(any());

        mockMvc.perform(post("/api/doctortype/")
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"typeName\":\"Dermatologist\"}"))
                .andExpect(status().isCreated());
    }
}

