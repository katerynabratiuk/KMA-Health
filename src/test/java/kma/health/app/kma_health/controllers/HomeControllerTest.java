package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.DoctorSearchService;
import kma.health.app.kma_health.service.DoctorTypeService;
import kma.health.app.kma_health.service.HospitalSearchService;
import kma.health.app.kma_health.service.HospitalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorSearchService doctorSearchService;

    @MockitoBean
    private HospitalSearchService hospitalSearchService;

    @MockitoBean
    private HospitalService hospitalService;

    @MockitoBean
    private DoctorTypeService doctorTypeService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void testHome_Success() throws Exception {
        when(hospitalSearchService.searchHospitals(any(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        mockMvc.perform(get("/ui/public/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("formDto", "cities", "specialties"));
    }

    @Test
    void testProcessSearch_Clinic() throws Exception {
        when(hospitalSearchService.searchHospitals(any(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        mockMvc.perform(post("/ui/public/search")
                        .with(csrf())
                        .param("searchType", "clinic")
                        .param("query", "test")
                        .param("sort", "rating-asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("searchPerformed", true));
    }

    @Test
    void testProcessSearch_Doctor() throws Exception {
        when(doctorSearchService.searchDoctors(any(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        mockMvc.perform(post("/ui/public/search")
                        .with(csrf())
                        .param("searchType", "doctor")
                        .param("query", "Dr. Smith")
                        .param("sort", "distance-dsc"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void testProcessSearch_HospitalError() throws Exception {
        when(hospitalSearchService.searchHospitals(any(), anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException("Search failed"));
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        mockMvc.perform(post("/ui/public/search")
                        .with(csrf())
                        .param("searchType", "clinic")
                        .param("sort", "rating-asc"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("searchError"));
    }

    @Test
    void testProcessSearch_DoctorError() throws Exception {
        when(doctorSearchService.searchDoctors(any(), anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException("Search failed"));
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        mockMvc.perform(post("/ui/public/search")
                        .with(csrf())
                        .param("searchType", "doctor")
                        .param("sort", "rating-asc"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("searchError"));
    }

    @Test
    void testProcessSearch_EmptySort() throws Exception {
        when(doctorSearchService.searchDoctors(any(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        mockMvc.perform(post("/ui/public/search")
                        .with(csrf())
                        .param("searchType", "doctor")
                        .param("sort", ""))
                .andExpect(status().isOk());
    }

    @Test
    void testProcessSearch_NullQuery() throws Exception {
        when(doctorSearchService.searchDoctors(any(), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        mockMvc.perform(post("/ui/public/search")
                        .with(csrf())
                        .param("searchType", "doctor")
                        .param("sort", "rating-asc"))
                .andExpect(status().isOk());
    }
}
