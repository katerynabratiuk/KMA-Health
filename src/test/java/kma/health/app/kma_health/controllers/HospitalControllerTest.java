package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.api.HospitalController;
import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.service.HospitalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HospitalControllerTest {

    @Mock
    private HospitalService hospitalService;

    @InjectMocks
    private HospitalController controller;

    // GET /api/hospital tests
    @Test
    void testGetHospitals_WithoutParams_Success() {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setId(1L);
        hospitalDto.setName("Test Hospital");

        when(hospitalService.searchHospitals(isNull(), eq(20), eq(0)))
                .thenReturn(List.of(hospitalDto));

        List<HospitalDto> result = controller.getHospitals(null, 0, 20);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Hospital", result.get(0).getName());
    }

    @Test
    void testGetHospitals_WithNameParam_Success() {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setId(1L);
        hospitalDto.setName("Central Hospital");

        when(hospitalService.searchHospitals(eq("Central"), eq(20), eq(0)))
                .thenReturn(List.of(hospitalDto));

        List<HospitalDto> result = controller.getHospitals("Central", 0, 20);

        assertNotNull(result);
        assertEquals("Central Hospital", result.get(0).getName());
    }

    @Test
    void testGetHospitals_WithPagination_Success() {
        when(hospitalService.searchHospitals(isNull(), eq(10), eq(2)))
                .thenReturn(Collections.emptyList());

        List<HospitalDto> result = controller.getHospitals(null, 2, 10);

        assertNotNull(result);
        verify(hospitalService).searchHospitals(isNull(), eq(10), eq(2));
    }

    @Test
    void testGetHospitals_NegativePageNum_ShouldUseZero() {
        when(hospitalService.searchHospitals(isNull(), eq(20), eq(0)))
                .thenReturn(Collections.emptyList());

        controller.getHospitals(null, -5, 20);

        verify(hospitalService).searchHospitals(isNull(), eq(20), eq(0));
    }

    @Test
    void testGetHospitals_ZeroPageSize_ShouldUseOne() {
        when(hospitalService.searchHospitals(isNull(), eq(1), eq(0)))
                .thenReturn(Collections.emptyList());

        controller.getHospitals(null, 0, 0);

        verify(hospitalService).searchHospitals(isNull(), eq(1), eq(0));
    }

    @Test
    void testGetHospitals_NegativePageSize_ShouldUseOne() {
        when(hospitalService.searchHospitals(isNull(), eq(1), eq(0)))
                .thenReturn(Collections.emptyList());

        controller.getHospitals(null, 0, -10);

        verify(hospitalService).searchHospitals(isNull(), eq(1), eq(0));
    }

    // GET /api/hospital/{hospitalId} tests
    @Test
    void testGetHospital_Success() {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setId(1L);
        hospitalDto.setName("Test Hospital");
        hospitalDto.setAddress("Test Address");

        when(hospitalService.getHospital(1L)).thenReturn(hospitalDto);

        HospitalDto result = controller.getHospital(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Hospital", result.getName());
    }

    @Test
    void testGetHospital_DifferentId() {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setId(2L);
        hospitalDto.setName("Doctor Hospital");

        when(hospitalService.getHospital(2L)).thenReturn(hospitalDto);

        HospitalDto result = controller.getHospital(2L);

        assertEquals("Doctor Hospital", result.getName());
    }

    // POST /api/hospital/ tests
    @Test
    void testCreateHospital_Success() {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setName("New Hospital");
        hospitalDto.setAddress("New Address");

        doNothing().when(hospitalService).createHospital(any(HospitalDto.class));

        ResponseEntity<String> result = controller.createHospital(hospitalDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(hospitalService).createHospital(any(HospitalDto.class));
    }

    @Test
    void testCreateHospital_IllegalArgument_BadRequest() {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setName("");

        doThrow(new IllegalArgumentException("Hospital name is required"))
                .when(hospitalService).createHospital(any(HospitalDto.class));

        ResponseEntity<String> result = controller.createHospital(hospitalDto);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Hospital name is required", result.getBody());
    }
}
