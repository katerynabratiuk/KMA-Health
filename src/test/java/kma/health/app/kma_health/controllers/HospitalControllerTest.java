package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.api.HospitalController;
import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.security.SecurityConfig;
import kma.health.app.kma_health.service.HospitalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HospitalController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class HospitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospitalService hospitalService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithAnonymousUser
    void testGetAllHospitals() throws Exception {
        HospitalDto mockHospital = new HospitalDto();
        mockHospital.setId(1L);
        mockHospital.setName("Test Hospital");

        when(hospitalService.searchHospitals(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(mockHospital));

        mockMvc.perform(get("/api/hospital")
                        .param("name", "Test")
                        .param("pageNum", "0")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Hospital"));
    }

    @Test
    @WithAnonymousUser
    void testGetAllHospitals_NegativePageParams() throws Exception {
        when(hospitalService.searchHospitals(isNull(), eq(1), eq(0)))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/hospital")
                        .param("pageNum", "-5")
                        .param("pageSize", "-10"))
                .andExpect(status().isOk());

        verify(hospitalService).searchHospitals(isNull(), eq(1), eq(0));
    }

    @Test
    @WithAnonymousUser
    void testGetHospitalById() throws Exception {
        HospitalDto mockHospital = new HospitalDto();
        mockHospital.setId(1L);
        mockHospital.setName("Test Hospital");

        when(hospitalService.getHospital(1L)).thenReturn(mockHospital);

        mockMvc.perform(get("/api/hospital/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Hospital"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testCreateHospital_Success() throws Exception {
        doNothing().when(hospitalService).createHospital(any(HospitalDto.class));

        mockMvc.perform(post("/api/hospital/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Hospital\",\"city\":\"Kyiv\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testCreateHospital_InvalidArgument() throws Exception {
        doThrow(new IllegalArgumentException("Invalid data"))
                .when(hospitalService).createHospital(any(HospitalDto.class));

        mockMvc.perform(post("/api/hospital/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }
}
