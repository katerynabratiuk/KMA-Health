package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.EditHospitalRequest;
import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.dto.HospitalFormDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.entity.Examination;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.enums.HospitalType;
import kma.health.app.kma_health.exception.CoordinatesNotFoundException;
import kma.health.app.kma_health.repository.HospitalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    public void testDeleteHospital_ShouldDeleteExistingHospital() {
        Long hospitalId = 1L;
        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);

        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(hospital));

        hospitalService.deleteHospital(hospitalId);

        verify(hospitalRepository, times(1)).delete(hospital);
    }

    @Test
    public void testCreateHospital_ShouldSaveNewHospital() {
        HospitalDto dto = new HospitalDto();
        dto.setName("Test Hospital");
        dto.setAddress("Test Address");
        dto.setCity("Kyiv");
        dto.setType(HospitalType.PUBLIC);

        HospitalGeocodingService.Coordinates coords = new HospitalGeocodingService.Coordinates(50.4501, 30.5234);

        when(hospitalGeocodingService.getCoordinatesByAddress("Test Address")).thenReturn(coords);

        hospitalService.createHospital(dto);

        verify(hospitalRepository, times(1)).save(any(Hospital.class));
    }

    @Test
    public void testCreateHospital_ShouldThrowExceptionWhenCoordinatesNotFound() {
        HospitalDto dto = new HospitalDto();
        dto.setName("Test Hospital");
        dto.setAddress("Invalid Address");

        when(hospitalGeocodingService.getCoordinatesByAddress("Invalid Address"))
                .thenThrow(new CoordinatesNotFoundException("Coordinates not found"));

        assertThrows(IllegalArgumentException.class, () -> {
            hospitalService.createHospital(dto);
        });

        verify(hospitalRepository, never()).save(any(Hospital.class));
    }

    @Test
    public void testEditHospitalAddress_ShouldUpdateAddress() {
        Long hospitalId = 1L;
        EditHospitalRequest request = new EditHospitalRequest();
        request.setId(hospitalId);
        request.setAddress("New Address");
        request.setCity("Lviv");

        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        hospital.setAddress("Old Address");

        HospitalGeocodingService.Coordinates coords = new HospitalGeocodingService.Coordinates(49.8397, 24.0297);

        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(hospital));
        when(hospitalGeocodingService.getCoordinatesByAddress("New Address")).thenReturn(coords);

        hospitalService.editHospitalAddress(request);

        verify(hospitalRepository, times(1)).save(hospital);
        assertEquals("New Address", hospital.getAddress());
        assertEquals("Lviv", hospital.getCity());
    }

    @Test
    public void testEditHospitalAddress_ShouldThrowExceptionWhenNotFound() {
        EditHospitalRequest request = new EditHospitalRequest();
        request.setId(999L);
        request.setAddress("New Address");

        when(hospitalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            hospitalService.editHospitalAddress(request);
        });
    }

    @Test
    public void testSearchHospitals_ShouldReturnHospitalsList() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setType(HospitalType.PUBLIC);

        Page<Hospital> page = new PageImpl<>(Collections.singletonList(hospital));

        when(hospitalRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<HospitalDto> result = hospitalService.searchHospitals("Test", 20, 0);

        assertEquals(1, result.size());
    }

    @Test
    public void testSearchHospitals_WithNullParameters() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Hospital");
        hospital.setType(HospitalType.PUBLIC);

        Page<Hospital> page = new PageImpl<>(Collections.singletonList(hospital));

        when(hospitalRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<HospitalDto> result = hospitalService.searchHospitals(null, null, null);

        assertNotNull(result);
    }

    @Test
    public void testGetAllHospitals_ShouldReturnAllHospitals() {
        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);
        hospital1.setName("Hospital 1");

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setName("Hospital 2");

        when(hospitalRepository.findAll()).thenReturn(Arrays.asList(hospital1, hospital2));

        List<HospitalFormDto> result = hospitalService.getAllHospitals();

        assertEquals(2, result.size());
    }

    @Test
    public void testProvidesExamination_ShouldReturnTrueWhenExaminationExists() {
        Examination examination = new Examination();
        examination.setId(1L);

        Hospital hospital = new Hospital();
        hospital.setExaminations(Collections.singleton(examination));

        assertTrue(hospitalService.providesExamination(hospital, examination));
    }

    @Test
    public void testProvidesExamination_ShouldReturnFalseWhenExaminationNotExists() {
        Examination examination1 = new Examination();
        examination1.setId(1L);

        Examination examination2 = new Examination();
        examination2.setId(2L);

        Hospital hospital = new Hospital();
        hospital.setExaminations(Collections.singleton(examination1));

        assertFalse(hospitalService.providesExamination(hospital, examination2));
    }

    @Test
    public void testProvidesExamination_ShouldReturnFalseWhenHospitalIsNull() {
        Examination examination = new Examination();
        assertFalse(hospitalService.providesExamination(null, examination));
    }

    @Test
    public void testProvidesExamination_ShouldReturnFalseWhenExaminationIsNull() {
        Hospital hospital = new Hospital();
        assertFalse(hospitalService.providesExamination(hospital, null));
    }

    @Test
    public void testProvidesDoctorType_ShouldReturnTrueWhenDoctorTypeExists() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);

        Doctor doctor = new Doctor();
        doctor.setHospital(hospital);

        DoctorType doctorType = new DoctorType();
        doctorType.setDoctors(new java.util.HashSet<>(Collections.singletonList(doctor)));

        assertTrue(hospitalService.providesDoctorType(hospital, doctorType));
    }

    @Test
    public void testProvidesDoctorType_ShouldReturnFalseWhenDoctorTypeNotExists() {
        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);

        Doctor doctor = new Doctor();
        doctor.setHospital(hospital2);

        DoctorType doctorType = new DoctorType();
        doctorType.setDoctors(new java.util.HashSet<>(Collections.singletonList(doctor)));

        assertFalse(hospitalService.providesDoctorType(hospital1, doctorType));
    }

    @Test
    public void testProvidesDoctorType_ShouldReturnFalseWhenHospitalIsNull() {
        DoctorType doctorType = new DoctorType();
        assertFalse(hospitalService.providesDoctorType(null, doctorType));
    }

    @Test
    public void testProvidesDoctorType_ShouldReturnFalseWhenDoctorTypeIsNull() {
        Hospital hospital = new Hospital();
        assertFalse(hospitalService.providesDoctorType(hospital, null));
    }

    @Test
    public void testGetAllCities_ShouldReturnDistinctCities() {
        Hospital hospital1 = new Hospital();
        hospital1.setCity("Kyiv");

        Hospital hospital2 = new Hospital();
        hospital2.setCity("Lviv");

        Hospital hospital3 = new Hospital();
        hospital3.setCity("Kyiv");

        when(hospitalRepository.findAll()).thenReturn(Arrays.asList(hospital1, hospital2, hospital3));

        List<String> result = hospitalService.getAllCities();

        assertEquals(2, result.size());
        assertTrue(result.contains("Kyiv"));
        assertTrue(result.contains("Lviv"));
    }

    @Test
    public void testGetAllCities_ShouldReturnEmptyListWhenNoHospitals() {
        when(hospitalRepository.findAll()).thenReturn(Collections.emptyList());

        List<String> result = hospitalService.getAllCities();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testEditHospitalAddress_ShouldThrowExceptionWhenCoordinatesNotFound() {
        Long hospitalId = 1L;
        EditHospitalRequest request = new EditHospitalRequest();
        request.setId(hospitalId);
        request.setAddress("Invalid Address");
        request.setCity("Unknown");

        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);

        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(hospital));
        when(hospitalGeocodingService.getCoordinatesByAddress("Invalid Address"))
                .thenThrow(new CoordinatesNotFoundException("Cannot find coordinates"));

        assertThrows(IllegalArgumentException.class, () -> {
            hospitalService.editHospitalAddress(request);
        });

        verify(hospitalRepository, never()).save(any(Hospital.class));
    }

    @Test
    public void testProvidesExamination_ShouldReturnFalseWhenExaminationsIsNull() {
        Hospital hospital = new Hospital();
        hospital.setExaminations(null);

        Examination examination = new Examination();
        examination.setId(1L);

        assertFalse(hospitalService.providesExamination(hospital, examination));
    }

    @Test
    public void testProvidesDoctorType_ShouldReturnFalseWhenDoctorsIsNull() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);

        DoctorType doctorType = new DoctorType();
        doctorType.setDoctors(null);

        assertFalse(hospitalService.providesDoctorType(hospital, doctorType));
    }

    @Test
    public void testSearchHospitals_WithEmptyName() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Hospital");
        hospital.setType(HospitalType.PUBLIC);

        Page<Hospital> page = new PageImpl<>(Collections.singletonList(hospital));

        when(hospitalRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<HospitalDto> result = hospitalService.searchHospitals("", 10, 0);

        assertNotNull(result);
    }

    @Test
    public void testSearchHospitals_WithBlankName() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Hospital");
        hospital.setType(HospitalType.PUBLIC);

        Page<Hospital> page = new PageImpl<>(Collections.singletonList(hospital));

        when(hospitalRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<HospitalDto> result = hospitalService.searchHospitals("   ", 10, 0);

        assertNotNull(result);
    }

    @Test
    public void testSearchHospitals_WithNegativePageNumber() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Hospital");
        hospital.setType(HospitalType.PUBLIC);

        Page<Hospital> page = new PageImpl<>(Collections.singletonList(hospital));

        when(hospitalRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<HospitalDto> result = hospitalService.searchHospitals("Test", 10, -1);

        assertNotNull(result);
    }

    @Test
    public void testSearchHospitals_WithZeroPageSize() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Hospital");
        hospital.setType(HospitalType.PUBLIC);

        Page<Hospital> page = new PageImpl<>(Collections.singletonList(hospital));

        when(hospitalRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        List<HospitalDto> result = hospitalService.searchHospitals("Test", 0, 0);

        assertNotNull(result);
    }

    @Test
    public void testGetHospital_ShouldReturnHospitalDto() {
        Long hospitalId = 1L;
        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        hospital.setName("Test Hospital");
        hospital.setType(HospitalType.PUBLIC);

        when(hospitalRepository.getReferenceById(hospitalId)).thenReturn(hospital);

        HospitalDto result = hospitalService.getHospital(hospitalId);

        assertNotNull(result);
        assertEquals("Test Hospital", result.getName());
    }
}
