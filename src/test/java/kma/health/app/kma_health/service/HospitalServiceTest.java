package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.repository.HospitalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private HospitalGeocodingService hospitalGeocodingService;

    @InjectMocks
    private HospitalService hospitalService;

    @Test
    public void testDeleteHospital_ShouldThrowExceptionWhenHospitalNotFound() {
        Long hospitalId = 1L;

        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            hospitalService.deleteHospital(hospitalId);
        });

        assertEquals("Hospital not found", exception.getMessage());
        verify(hospitalRepository, times(1)).findById(hospitalId);
        verify(hospitalRepository, never()).delete(any(Hospital.class));
    }
}
