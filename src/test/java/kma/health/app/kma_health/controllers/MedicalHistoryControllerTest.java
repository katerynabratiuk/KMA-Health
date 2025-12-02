package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MedicalHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithAnonymousUser
    void testGetMedicalHistory_ShouldRequireAuthentication() throws Exception {
        UUID patientId = UUID.randomUUID();

        mockMvc.perform(get("/ui/history/{patientId}", patientId))
                .andExpect(status().is4xxClientError());
    }
}

