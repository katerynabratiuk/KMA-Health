package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.ui.HomeController;
import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.dto.HospitalSearchDto;
import kma.health.app.kma_health.dto.SearchFormDto;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.service.DoctorSearchService;
import kma.health.app.kma_health.service.DoctorTypeService;
import kma.health.app.kma_health.service.HospitalSearchService;
import kma.health.app.kma_health.service.HospitalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private DoctorSearchService doctorSearchService;

    @Mock
    private HospitalSearchService hospitalSearchService;

    @Mock
    private HospitalService hospitalService;

    @Mock
    private DoctorTypeService doctorTypeService;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController controller;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                "user",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testHome_AnonymousUser_Success() throws Exception {
        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.home(model);

        assertEquals("home", result);
        verify(model).addAttribute(eq("userRole"), isNull());
        verify(model).addAttribute(eq("searchPerformed"), eq(false));
    }

    @Test
    void testHome_AuthenticatedPatient_Success() throws Exception {
        setSecurityContext("PATIENT");
        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv", "Lviv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist", "Dentist"));

        String result = controller.home(model);

        assertEquals("home", result);
        verify(model).addAttribute("userRole", "PATIENT");
    }

    @Test
    void testHome_AuthenticatedDoctor_Success() throws Exception {
        setSecurityContext("DOCTOR");
        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.home(model);

        assertEquals("home", result);
        verify(model).addAttribute("userRole", "DOCTOR");
    }

    @Test
    void testProcessSearch_ClinicSearch_Success() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("clinic");
        formDto.setQuery("test");
        formDto.setSort("rating-asc");
        formDto.setUserLat(50.45);
        formDto.setUserLon(30.52);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(List.of(hospital));
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
        verify(model).addAttribute(eq("hospitals"), anyList());
        verify(model).addAttribute("doctors", null);
        verify(model).addAttribute("searchPerformed", true);
    }

    @Test
    void testProcessSearch_ClinicSearch_Exception() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("clinic");
        formDto.setSort("rating-asc");

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException("Search failed"));
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
        verify(model).addAttribute("hospitals", null);
        verify(model).addAttribute(eq("searchError"), anyString());
    }

    @Test
    void testProcessSearch_DoctorSearch_Success() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("doctor");
        formDto.setQuery("smith");
        formDto.setSort("name-desc");

        when(doctorSearchService.searchDoctors(any(DoctorSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
        verify(model).addAttribute(eq("doctors"), anyList());
        verify(model).addAttribute("hospitals", null);
    }

    @Test
    void testProcessSearch_DoctorSearch_Exception() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("doctor");

        when(doctorSearchService.searchDoctors(any(DoctorSearchDto.class), anyDouble(), anyDouble()))
                .thenThrow(new RuntimeException("Doctor search failed"));
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
        verify(model).addAttribute("doctors", null);
        verify(model).addAttribute(eq("searchError"), anyString());
    }

    @Test
    void testProcessSearch_SortWithoutDirection() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("clinic");
        formDto.setSort("rating");

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
    }

    @Test
    void testProcessSearch_NullSort() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("clinic");
        formDto.setSort(null);

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
    }

    @Test
    void testProcessSearch_NullQuery() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("clinic");
        formDto.setSort("rating-asc");
        formDto.setQuery(null);

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
    }

    @Test
    void testProcessSearch_WithCityAndDoctorType() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("doctor");
        formDto.setCity("Kyiv");
        formDto.setDoctorType("Cardiologist");
        formDto.setSort("distance-asc");

        when(doctorSearchService.searchDoctors(any(DoctorSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(List.of("Kyiv"));
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(List.of("Cardiologist"));

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
        verify(model).addAttribute("searchPerformed", true);
    }

    @Test
    void testHome_AuthenticatedWithNoRolePrefix() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                "user",
                null,
                List.of(new SimpleGrantedAuthority("CUSTOM_AUTHORITY"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.home(model);

        assertEquals("home", result);
        verify(model).addAttribute(eq("userRole"), isNull());
    }

    @Test
    void testHome_AuthenticatedWithEmptyAuthorities() throws Exception {
        var auth = new UsernamePasswordAuthenticationToken(
                "user",
                null,
                Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.home(model);

        assertEquals("home", result);
        verify(model).addAttribute(eq("userRole"), isNull());
    }

    @Test
    void testProcessSearch_SortWithEmptyDirection() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("clinic");
        formDto.setSort("rating-");

        when(hospitalSearchService.searchHospitals(any(HospitalSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
    }

    @Test
    void testProcessSearch_SortWithMultipleDashes() throws Exception {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("doctor");
        formDto.setSort("rating-asc-extra");

        when(doctorSearchService.searchDoctors(any(DoctorSearchDto.class), anyDouble(), anyDouble()))
                .thenReturn(Collections.emptyList());
        when(hospitalService.getAllCities()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypeNames()).thenReturn(Collections.emptyList());

        String result = controller.processSearch(formDto, model);

        assertEquals("home", result);
    }
}
