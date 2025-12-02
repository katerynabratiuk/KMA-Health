package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.repository.HospitalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NearestHospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private NearestHospitalService nearestHospitalService;

    @Test
    public void testFindNearestHospital_ShouldReturnNearestHospital() {
        Hospital nearHospital = new Hospital();
        nearHospital.setName("Near Hospital");
        nearHospital.setLatitude(50.4501);
        nearHospital.setLongitude(30.5234);

        Hospital farHospital = new Hospital();
        farHospital.setName("Far Hospital");
        farHospital.setLatitude(50.5000);
        farHospital.setLongitude(30.6000);

        when(hospitalRepository.findByCity("Kyiv")).thenReturn(Arrays.asList(farHospital, nearHospital));

        Hospital result = nearestHospitalService.findNearestHospital("Kyiv", "50.4500", "30.5200");

        assertNotNull(result);
        assertEquals("Near Hospital", result.getName());
    }

    @Test
    public void testFindNearestHospital_ShouldReturnNullWhenNoHospitals() {
        when(hospitalRepository.findByCity("Kyiv")).thenReturn(Collections.emptyList());

        assertThrows(EmptyResultDataAccessException.class, () -> {
            nearestHospitalService.findNearestHospital("Kyiv", "50.4500", "30.5200");
        });
    }

    @Test
    public void testSortHospitalsByUserCoordinates_ShouldSortByDistance() {
        Hospital hospital1 = new Hospital();
        hospital1.setName("Hospital 1");
        hospital1.setLatitude(50.4501);
        hospital1.setLongitude(30.5234);

        Hospital hospital2 = new Hospital();
        hospital2.setName("Hospital 2");
        hospital2.setLatitude(50.5000);
        hospital2.setLongitude(30.6000);

        Hospital hospital3 = new Hospital();
        hospital3.setName("Hospital 3");
        hospital3.setLatitude(50.4600);
        hospital3.setLongitude(30.5300);

        when(hospitalRepository.findByCity("Kyiv")).thenReturn(Arrays.asList(hospital2, hospital1, hospital3));

        LinkedList<Hospital> result = nearestHospitalService.sortHospitalsByUserCoordinates("Kyiv", "50.4500", "30.5200");

        assertEquals(3, result.size());
        assertEquals("Hospital 1", result.getFirst().getName());
    }

    @Test
    public void testSortHospitalsByUserCoordinates_ShouldThrowExceptionWhenNoHospitals() {
        when(hospitalRepository.findByCity("EmptyCity")).thenReturn(Collections.emptyList());

        assertThrows(EmptyResultDataAccessException.class, () -> {
            nearestHospitalService.sortHospitalsByUserCoordinates("EmptyCity", "50.4500", "30.5200");
        });
    }

    @Test
    public void testSortHospitalsByUserCoordinates_ShouldThrowExceptionForInvalidLatitude() {
        Hospital hospital = new Hospital();
        hospital.setLatitude(50.4501);
        hospital.setLongitude(30.5234);

        when(hospitalRepository.findByCity("Kyiv")).thenReturn(Collections.singletonList(hospital));

        assertThrows(IllegalArgumentException.class, () -> {
            nearestHospitalService.sortHospitalsByUserCoordinates("Kyiv", "invalid", "30.5200");
        });
    }

    @Test
    public void testSortHospitalsByUserCoordinates_ShouldThrowExceptionForInvalidLongitude() {
        Hospital hospital = new Hospital();
        hospital.setLatitude(50.4501);
        hospital.setLongitude(30.5234);

        when(hospitalRepository.findByCity("Kyiv")).thenReturn(Collections.singletonList(hospital));

        assertThrows(IllegalArgumentException.class, () -> {
            nearestHospitalService.sortHospitalsByUserCoordinates("Kyiv", "50.4500", "invalid");
        });
    }

    @Test
    public void testDistanceInKm_ShouldCalculateCorrectDistance() {
        double distance = NearestHospitalService.distanceInKm(50.4501, 30.5234, 50.5000, 30.6000);
        
        assertTrue(distance > 0);
        assertTrue(distance < 10);
    }

    @Test
    public void testDistanceInKm_ShouldReturnZeroForSamePoint() {
        double distance = NearestHospitalService.distanceInKm(50.4501, 30.5234, 50.4501, 30.5234);
        
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    public void testFindNearestHospital_ShouldReturnFirstWhenSingleHospital() {
        Hospital singleHospital = new Hospital();
        singleHospital.setName("Only Hospital");
        singleHospital.setLatitude(50.4501);
        singleHospital.setLongitude(30.5234);

        when(hospitalRepository.findByCity("Kyiv")).thenReturn(Collections.singletonList(singleHospital));

        Hospital result = nearestHospitalService.findNearestHospital("Kyiv", "50.4500", "30.5200");

        assertNotNull(result);
        assertEquals("Only Hospital", result.getName());
    }

    @Test
    public void testSortHospitalsByUserCoordinates_SortsByProximity() {
        Hospital hospital1 = new Hospital();
        hospital1.setName("Far Hospital");
        hospital1.setLatitude(52.0);
        hospital1.setLongitude(32.0);

        Hospital hospital2 = new Hospital();
        hospital2.setName("Near Hospital");
        hospital2.setLatitude(50.4505);
        hospital2.setLongitude(30.5205);

        Hospital hospital3 = new Hospital();
        hospital3.setName("Medium Hospital");
        hospital3.setLatitude(50.5);
        hospital3.setLongitude(30.6);

        when(hospitalRepository.findByCity("Kyiv")).thenReturn(Arrays.asList(hospital1, hospital2, hospital3));

        LinkedList<Hospital> result = nearestHospitalService.sortHospitalsByUserCoordinates("Kyiv", "50.4500", "30.5200");

        assertEquals("Near Hospital", result.get(0).getName());
        assertEquals("Medium Hospital", result.get(1).getName());
        assertEquals("Far Hospital", result.get(2).getName());
    }

    @Test
    public void testDistanceInKm_LongDistance() {
        double distance = NearestHospitalService.distanceInKm(50.45, 30.52, 49.84, 24.02);
        
        assertTrue(distance > 400);
        assertTrue(distance < 600);
    }
}

