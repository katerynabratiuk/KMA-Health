package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.DoctorTypeDto;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.repository.DoctorTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorTypeServiceTest {

    @Mock
    private DoctorTypeRepository doctorTypeRepository;

    @InjectMocks
    private DoctorTypeService doctorTypeService;

    @Test
    public void testCreateDoctorType_ShouldSaveNewDoctorType() {
        DoctorTypeDto dto = new DoctorTypeDto("Cardiologist");

        when(doctorTypeRepository.findByTypeName("Cardiologist")).thenReturn(Optional.empty());

        doctorTypeService.createDoctorType(dto);

        verify(doctorTypeRepository, times(1)).save(any(DoctorType.class));
    }

    @Test
    public void testCreateDoctorType_ShouldThrowExceptionWhenTypeExists() {
        DoctorTypeDto dto = new DoctorTypeDto("Cardiologist");

        DoctorType existingType = new DoctorType();
        existingType.setTypeName("Cardiologist");

        when(doctorTypeRepository.findByTypeName("Cardiologist")).thenReturn(Optional.of(existingType));

        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorTypeService.createDoctorType(dto);
        });

        verify(doctorTypeRepository, never()).save(any(DoctorType.class));
    }

    @Test
    public void testDeleteDoctorType_ShouldDeleteExistingType() {
        DoctorTypeDto dto = new DoctorTypeDto("Cardiologist");

        DoctorType existingType = new DoctorType();
        existingType.setTypeName("Cardiologist");

        when(doctorTypeRepository.findByTypeName("Cardiologist")).thenReturn(Optional.of(existingType));

        doctorTypeService.deleteDoctorType(dto);

        verify(doctorTypeRepository, times(1)).delete(existingType);
    }

    @Test
    public void testDeleteDoctorType_ShouldThrowExceptionWhenTypeNotFound() {
        DoctorTypeDto dto = new DoctorTypeDto("NonExistent");

        when(doctorTypeRepository.findByTypeName("NonExistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            doctorTypeService.deleteDoctorType(dto);
        });

        verify(doctorTypeRepository, never()).delete(any(DoctorType.class));
    }

    @Test
    public void testGetAllDoctorTypes_ShouldReturnAllTypes() {
        DoctorType type1 = new DoctorType();
        type1.setTypeName("Cardiologist");
        DoctorType type2 = new DoctorType();
        type2.setTypeName("Neurologist");

        when(doctorTypeRepository.findAll()).thenReturn(Arrays.asList(type1, type2));

        List<DoctorType> result = doctorTypeService.getAllDoctorTypes();

        assertEquals(2, result.size());
        verify(doctorTypeRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllDoctorTypes_ShouldReturnEmptyListWhenNoTypes() {
        when(doctorTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<DoctorType> result = doctorTypeService.getAllDoctorTypes();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAllDoctorTypeNames_ShouldReturnAllTypeNames() {
        DoctorType type1 = new DoctorType();
        type1.setTypeName("Cardiologist");
        DoctorType type2 = new DoctorType();
        type2.setTypeName("Neurologist");

        when(doctorTypeRepository.findAll()).thenReturn(Arrays.asList(type1, type2));

        List<String> result = doctorTypeService.getAllDoctorTypeNames();

        assertEquals(2, result.size());
        assertTrue(result.contains("Cardiologist"));
        assertTrue(result.contains("Neurologist"));
    }

    @Test
    public void testGetAllDoctorTypeNames_ShouldReturnEmptyListWhenNoTypes() {
        when(doctorTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<String> result = doctorTypeService.getAllDoctorTypeNames();

        assertTrue(result.isEmpty());
    }
}

