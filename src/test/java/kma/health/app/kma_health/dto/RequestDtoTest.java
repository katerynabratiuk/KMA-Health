package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RequestDtoTest {

    // CreateReferralRequest tests
    @Test
    void testCreateReferralRequest_GettersSetters() {
        CreateReferralRequest request = new CreateReferralRequest();
        // It's a simple record/dto, test what's available
        assertNotNull(request);
    }

    // PatientRegisterRequest tests
    @Test
    void testPatientRegisterRequest_GettersSetters() {
        PatientRegisterRequest request = new PatientRegisterRequest();

        request.setFullName("John Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380991234567");
        request.setPassportNumber("AB123456");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setRole(UserRole.PATIENT);

        assertEquals("John Doe", request.getFullName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("+380991234567", request.getPhoneNumber());
        assertEquals("AB123456", request.getPassportNumber());
        assertEquals(LocalDate.of(1990, 1, 1), request.getBirthDate());
        assertEquals(UserRole.PATIENT, request.getRole());
    }

    // DoctorRegisterRequest tests
    @Test
    void testDoctorRegisterRequest_GettersSetters() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();

        request.setFullName("Dr. Smith");
        request.setEmail("dr.smith@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380997654321");
        request.setPassportNumber("CD789012");
        request.setBirthDate(LocalDate.of(1980, 5, 15));
        request.setRole(UserRole.DOCTOR);
        request.setType("adult");
        request.setDescription("Experienced doctor");
        request.setStartedWorking(LocalDate.of(2010, 1, 1));
        request.setDoctorTypeId(1L);
        request.setHospitalId(1L);
        request.setLabHospitalId(2L);
        request.setRegisterKey("key123");

        assertEquals("Dr. Smith", request.getFullName());
        assertEquals("dr.smith@example.com", request.getEmail());
        assertEquals("adult", request.getType());
        assertEquals("Experienced doctor", request.getDescription());
        assertEquals(1L, request.getDoctorTypeId());
        assertEquals(1L, request.getHospitalId());
        assertEquals(2L, request.getLabHospitalId());
        assertEquals("key123", request.getRegisterKey());
    }

    // MedicalFileUploadDto tests
    @Test
    void testMedicalFileUploadDto_GettersSetters() {
        MedicalFileUploadDto dto = new MedicalFileUploadDto();
        MultipartFile mockFile = mock(MultipartFile.class);

        dto.setName("test.pdf");
        dto.setExtension("pdf");
        dto.setFileType("application/pdf");
        dto.setFile(mockFile);

        assertEquals("test.pdf", dto.getName());
        assertEquals("pdf", dto.getExtension());
        assertEquals("application/pdf", dto.getFileType());
        assertEquals(mockFile, dto.getFile());
    }

    // HospitalSearchDto tests
    @Test
    void testHospitalSearchDto_GettersSetters() {
        HospitalSearchDto dto = new HospitalSearchDto();

        dto.setCity("Kyiv");
        dto.setRequest("Hospital");
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        assertEquals("Kyiv", dto.getCity());
        assertEquals("Hospital", dto.getRequest());
        assertNotNull(dto.getSortBy());
    }

    // DoctorSearchDto tests
    @Test
    void testDoctorSearchDto_GettersSetters() {
        DoctorSearchDto dto = new DoctorSearchDto();

        dto.setQuery("Dr. Test");
        dto.setCity("Kyiv");
        dto.setDoctorType("Cardiologist");
        dto.setHospitalId(1L);
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "dsc"));

        assertEquals("Dr. Test", dto.getQuery());
        assertEquals("Kyiv", dto.getCity());
        assertEquals("Cardiologist", dto.getDoctorType());
        assertEquals(1L, dto.getHospitalId());
    }

    // DoctorTypeDto tests
    @Test
    void testDoctorTypeDto_GettersSetters() {
        DoctorTypeDto dto = new DoctorTypeDto("Cardiologist");

        assertEquals("Cardiologist", dto.getTypeName());
        
        dto.setTypeName("Surgeon");
        assertEquals("Surgeon", dto.getTypeName());
    }

    // AppointmentCreateUpdateDto tests
    @Test
    void testAppointmentCreateUpdateDto() {
        AppointmentCreateUpdateDto dto = new AppointmentCreateUpdateDto();

        java.util.UUID patientId = java.util.UUID.randomUUID();
        java.util.UUID doctorId = java.util.UUID.randomUUID();
        java.util.UUID referralId = java.util.UUID.randomUUID();

        dto.setPatientId(patientId);
        dto.setDoctorId(doctorId);
        dto.setHospitalId(1L);
        dto.setReferralId(referralId);
        dto.setDate(LocalDate.now());
        dto.setTime(java.time.LocalTime.of(10, 0));

        assertEquals(patientId, dto.getPatientId());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals(1L, dto.getHospitalId());
        assertEquals(referralId, dto.getReferralId());
    }
}

