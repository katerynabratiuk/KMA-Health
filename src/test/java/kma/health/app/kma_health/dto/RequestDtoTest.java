package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.enums.HospitalType;
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

    // ExaminationDto tests
    @Test
    void testExaminationDto_AllArgsConstructor() {
        ExaminationDto dto = new ExaminationDto("Blood Test", "ml");

        assertEquals("Blood Test", dto.getName());
        assertEquals("ml", dto.getUnit());
    }

    @Test
    void testExaminationDto_SettersGetters() {
        ExaminationDto dto = new ExaminationDto("Test", "unit");

        dto.setName("X-Ray");
        dto.setUnit("image");

        assertEquals("X-Ray", dto.getName());
        assertEquals("image", dto.getUnit());
    }

    // SearchFormDto tests
    @Test
    void testSearchFormDto_DefaultValues() {
        SearchFormDto dto = new SearchFormDto();

        assertEquals("doctor", dto.getSearchType());
        assertEquals("rating-asc", dto.getSort());
        assertEquals(0, dto.getUserLat());
        assertEquals(0, dto.getUserLon());
    }

    @Test
    void testSearchFormDto_SettersGetters() {
        SearchFormDto dto = new SearchFormDto();

        dto.setSearchType("hospital");
        dto.setQuery("Test");
        dto.setDoctorType("Cardiologist");
        dto.setCity("Kyiv");
        dto.setSort("distance-dsc");
        dto.setUserLat(50.45);
        dto.setUserLon(30.52);

        assertEquals("hospital", dto.getSearchType());
        assertEquals("Test", dto.getQuery());
        assertEquals("Cardiologist", dto.getDoctorType());
        assertEquals("Kyiv", dto.getCity());
        assertEquals("distance-dsc", dto.getSort());
        assertEquals(50.45, dto.getUserLat());
        assertEquals(30.52, dto.getUserLon());
    }

    // PatientContactsDto tests
    @Test
    void testPatientContactsDto_DefaultConstructor() {
        PatientContactsDto dto = new PatientContactsDto();
        assertNotNull(dto);
    }

    @Test
    void testPatientContactsDto_AllArgsConstructor() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        PatientContactsDto dto = new PatientContactsDto(
                "John Doe",
                "+380991234567",
                "john@example.com",
                "Dr. Family",
                birthDate
        );

        assertNotNull(dto);
    }

    @Test
    void testPatientContactsDto_Setters() {
        PatientContactsDto dto = new PatientContactsDto();

        dto.setFullName("Jane Doe");
        dto.setPhone("+380997654321");
        dto.setEmail("jane@example.com");
        dto.setFamilyDoctorName("Dr. Smith");
        dto.setBirthDate(LocalDate.of(1985, 5, 15));

        // Only setters are available (no getters defined)
        assertNotNull(dto);
    }

    // CreateReferralRequest tests - it only has getters
    @Test
    void testCreateReferralRequest_Getters() {
        CreateReferralRequest request = new CreateReferralRequest();

        // Verify getters exist and return null for uninitialized fields
        assertNull(request.getPatientId());
        assertNull(request.getDoctorTypeName());
        assertNull(request.getExaminationId());
    }

    // DoctorRegisterRequest builder test
    @Test
    void testDoctorRegisterRequest_Builder() {
        DoctorRegisterRequest request = DoctorRegisterRequest.builder()
                .role(UserRole.DOCTOR)
                .fullName("Dr. Builder")
                .email("builder@example.com")
                .password("password123")
                .phoneNumber("+380991234567")
                .passportNumber("123456789")
                .birthDate(LocalDate.of(1980, 1, 1))
                .startedWorking(LocalDate.of(2010, 1, 1))
                .type("adult")
                .description("Test")
                .doctorTypeId(1L)
                .hospitalId(1L)
                .registerKey("key")
                .build();

        assertEquals("Dr. Builder", request.getFullName());
        assertEquals(UserRole.DOCTOR, request.getRole());
    }

    // MedicalFileUploadDto AllArgsConstructor
    @Test
    void testMedicalFileUploadDto_AllArgsConstructor() {
        java.util.UUID id = java.util.UUID.randomUUID();
        MultipartFile mockFile = mock(MultipartFile.class);

        MedicalFileUploadDto dto = new MedicalFileUploadDto(
                id,
                "document",
                "test.pdf",
                "pdf",
                mockFile
        );

        assertEquals(id, dto.getId());
        assertEquals("document", dto.getFileType());
        assertEquals("test.pdf", dto.getName());
        assertEquals("pdf", dto.getExtension());
        assertEquals(mockFile, dto.getFile());
    }

    // EditHospitalRequest tests
    @Test
    void testEditHospitalRequest_DefaultConstructor() {
        EditHospitalRequest request = new EditHospitalRequest();
        assertNotNull(request);
    }

    @Test
    void testEditHospitalRequest_AllArgsConstructor() {
        EditHospitalRequest request = new EditHospitalRequest(
                1L,
                "Test Hospital",
                "Test Address",
                "Kyiv"
        );

        assertEquals(1L, request.getId());
        assertEquals("Test Hospital", request.getName());
        assertEquals("Test Address", request.getAddress());
        assertEquals("Kyiv", request.getCity());
    }

    @Test
    void testEditHospitalRequest_SettersGetters() {
        EditHospitalRequest request = new EditHospitalRequest();

        request.setId(2L);
        request.setName("New Hospital");
        request.setAddress("New Address");
        request.setCity("Lviv");

        assertEquals(2L, request.getId());
        assertEquals("New Hospital", request.getName());
        assertEquals("New Address", request.getAddress());
        assertEquals("Lviv", request.getCity());
    }

    // HospitalSearchDto extended tests
    @Test
    void testHospitalSearchDto_DefaultValues() {
        HospitalSearchDto dto = new HospitalSearchDto();

        assertNotNull(dto.getSortBy());
        assertEquals("rating", dto.getSortBy().getParam());
        assertEquals("asc", dto.getSortBy().getDirection());
    }

    @Test
    void testHospitalSearchDto_AllFields() {
        HospitalSearchDto dto = new HospitalSearchDto();

        dto.setRequest("City Hospital");
        dto.setCity("Kyiv");
        dto.setHospitalType(HospitalType.PUBLIC);
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "dsc"));

        assertEquals("City Hospital", dto.getRequest());
        assertEquals("Kyiv", dto.getCity());
        assertEquals(HospitalType.PUBLIC, dto.getHospitalType());
        assertEquals("distance", dto.getSortBy().getParam());
        assertEquals("dsc", dto.getSortBy().getDirection());
    }

    @Test
    void testHospitalSearchDto_PrivateHospitalType() {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setHospitalType(HospitalType.PRIVATE);

        assertEquals(HospitalType.PRIVATE, dto.getHospitalType());
    }

    // DoctorTypeDto extended tests
    @Test
    void testDoctorTypeDto_AllArgsConstructor() {
        DoctorTypeDto dto = new DoctorTypeDto("Surgeon");

        assertEquals("Surgeon", dto.getTypeName());
    }

    // DoctorSearchDto.SortBy tests
    @Test
    void testSortBy_AllArgsConstructor() {
        DoctorSearchDto.SortBy sortBy = new DoctorSearchDto.SortBy("name", "asc");

        assertEquals("name", sortBy.getParam());
        assertEquals("asc", sortBy.getDirection());
    }

    @Test
    void testSortBy_SettersGetters() {
        DoctorSearchDto.SortBy sortBy = new DoctorSearchDto.SortBy("rating", "asc");

        sortBy.setParam("distance");
        sortBy.setDirection("dsc");

        assertEquals("distance", sortBy.getParam());
        assertEquals("dsc", sortBy.getDirection());
    }

    @Test
    void testSortBy_DefaultConstructor() {
        DoctorSearchDto.SortBy sortBy = new DoctorSearchDto.SortBy();
        assertNotNull(sortBy);
    }
}

