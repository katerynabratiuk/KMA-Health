package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.api.HospitalController;
import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.HospitalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HospitalController.class)
@AutoConfigureMockMvc(addFilters = false)
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
}
