package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.entity.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackDtoTest {

    @Test
    void testFeedbackViewDto_FromDoctorFeedback() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());

        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setScore((short) 5);
        feedback.setComment("Great doctor!");
        feedback.setPatient(patient);
        feedback.setDoctor(doctor);
        feedback.setDate(LocalDate.now());

        FeedbackViewDto dto = FeedbackViewDto.fromEntity(feedback);

        assertEquals((short) 5, dto.getScore());
        assertEquals("Great doctor!", dto.getComment());
        assertEquals(doctor.getId(), dto.getDoctor_id());
        assertEquals(patient.getId(), dto.getPatient_id());
    }

    @Test
    void testFeedbackViewDto_FromHospitalFeedback() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Hospital hospital = new Hospital();
        hospital.setId(1L);

        Feedback feedback = new Feedback();
        feedback.setId(2L);
        feedback.setScore((short) 4);
        feedback.setComment("Good hospital");
        feedback.setPatient(patient);
        feedback.setHospital(hospital);
        feedback.setDate(LocalDate.now());

        FeedbackViewDto dto = FeedbackViewDto.fromEntity(feedback);

        assertEquals((short) 4, dto.getScore());
        assertEquals("Good hospital", dto.getComment());
        assertEquals(1L, dto.getHospital_id());
    }

    @Test
    void testFeedbackViewDto_ToEntity() {
        FeedbackViewDto dto = new FeedbackViewDto();
        dto.setScore((short) 5);
        dto.setComment("Excellent");
        dto.setDoctor_id(UUID.randomUUID());
        dto.setPatient_id(UUID.randomUUID());

        Feedback entity = FeedbackViewDto.toEntity(dto);

        assertEquals((short) 5, entity.getScore());
        assertEquals("Excellent", entity.getComment());
    }

    @Test
    void testFeedbackViewDto_ToEntity_WithHospital() {
        FeedbackViewDto dto = new FeedbackViewDto();
        dto.setScore((short) 4);
        dto.setComment("Good hospital");
        dto.setHospital_id(1L);
        dto.setPatient_id(UUID.randomUUID());

        Feedback entity = FeedbackViewDto.toEntity(dto);

        assertEquals((short) 4, entity.getScore());
        assertNotNull(entity.getHospital());
    }

    @Test
    void testFeedbackCreateUpdateDto_SettersAndGetters() {
        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();

        dto.setScore((short) 5);
        dto.setComment("Excellent service");
        dto.setDoctor_id(UUID.randomUUID());
        dto.setHospital_id(1L);
        dto.setPatient_id(UUID.randomUUID());

        assertEquals((short) 5, dto.getScore());
        assertEquals("Excellent service", dto.getComment());
        assertNotNull(dto.getDoctor_id());
        assertEquals(1L, dto.getHospital_id());
        assertNotNull(dto.getPatient_id());
    }

    @Test
    void testFeedbackCreateUpdateDto_FromEntity() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());

        Feedback feedback = new Feedback();
        feedback.setScore((short) 4);
        feedback.setComment("Good");
        feedback.setPatient(patient);
        feedback.setDoctor(doctor);

        FeedbackCreateUpdateDto dto = FeedbackCreateUpdateDto.fromEntity(feedback);

        assertEquals((short) 4, dto.getScore());
        assertEquals("Good", dto.getComment());
        assertEquals(doctor.getId(), dto.getDoctor_id());
    }

    @Test
    void testFeedbackCreateUpdateDto_ToEntity() {
        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setScore((short) 5);
        dto.setComment("Great");
        dto.setDoctor_id(UUID.randomUUID());
        dto.setPatient_id(UUID.randomUUID());

        Feedback entity = FeedbackCreateUpdateDto.toEntity(dto);

        assertEquals((short) 5, entity.getScore());
        assertEquals("Great", entity.getComment());
    }

    // Additional tests for missing branches
    @Test
    void testFeedbackCreateUpdateDto_FromEntity_WithHospital() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Hospital hospital = new Hospital();
        hospital.setId(1L);

        Feedback feedback = new Feedback();
        feedback.setScore((short) 4);
        feedback.setComment("Nice hospital");
        feedback.setPatient(patient);
        feedback.setDoctor(null);  // No doctor
        feedback.setHospital(hospital);

        FeedbackCreateUpdateDto dto = FeedbackCreateUpdateDto.fromEntity(feedback);

        assertEquals((short) 4, dto.getScore());
        assertEquals("Nice hospital", dto.getComment());
        assertNull(dto.getDoctor_id());
        assertEquals(1L, dto.getHospital_id());
    }

    @Test
    void testFeedbackCreateUpdateDto_FromEntity_WithNeitherDoctorNorHospital() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Feedback feedback = new Feedback();
        feedback.setScore((short) 3);
        feedback.setComment("Test");
        feedback.setPatient(patient);
        feedback.setDoctor(null);
        feedback.setHospital(null);

        FeedbackCreateUpdateDto dto = FeedbackCreateUpdateDto.fromEntity(feedback);

        assertEquals((short) 3, dto.getScore());
        assertNull(dto.getDoctor_id());
        assertNull(dto.getHospital_id());
    }

    @Test
    void testFeedbackCreateUpdateDto_ToEntity_WithHospital() {
        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setScore((short) 4);
        dto.setComment("Good hospital");
        dto.setDoctor_id(null);  // No doctor
        dto.setHospital_id(1L);
        dto.setPatient_id(UUID.randomUUID());

        Feedback entity = FeedbackCreateUpdateDto.toEntity(dto);

        assertEquals((short) 4, entity.getScore());
        assertNull(entity.getDoctor());
        assertNotNull(entity.getHospital());
        assertEquals(1L, entity.getHospital().getId());
    }

    @Test
    void testFeedbackCreateUpdateDto_ToEntity_WithNeitherDoctorNorHospital() {
        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto();
        dto.setScore((short) 3);
        dto.setComment("Test");
        dto.setDoctor_id(null);
        dto.setHospital_id(null);
        dto.setPatient_id(UUID.randomUUID());

        Feedback entity = FeedbackCreateUpdateDto.toEntity(dto);

        assertEquals((short) 3, entity.getScore());
        assertNull(entity.getDoctor());
        assertNull(entity.getHospital());
    }

    @Test
    void testFeedbackViewDto_FromEntity_WithNeitherDoctorNorHospital() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Feedback feedback = new Feedback();
        feedback.setScore((short) 3);
        feedback.setComment("Test");
        feedback.setPatient(patient);
        feedback.setDoctor(null);
        feedback.setHospital(null);

        FeedbackViewDto dto = FeedbackViewDto.fromEntity(feedback);

        assertEquals((short) 3, dto.getScore());
        assertNull(dto.getDoctor_id());
        assertNull(dto.getHospital_id());
    }

    @Test
    void testFeedbackViewDto_ToEntity_WithNeitherDoctorNorHospital() {
        FeedbackViewDto dto = new FeedbackViewDto();
        dto.setScore((short) 3);
        dto.setComment("Test");
        dto.setDoctor_id(null);
        dto.setHospital_id(null);
        dto.setPatient_id(UUID.randomUUID());

        Feedback entity = FeedbackViewDto.toEntity(dto);

        assertEquals((short) 3, entity.getScore());
        assertNull(entity.getDoctor());
        assertNull(entity.getHospital());
    }

    @Test
    void testFeedbackCreateUpdateDto_AllArgsConstructor() {
        LocalDate date = LocalDate.now();
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        FeedbackCreateUpdateDto dto = new FeedbackCreateUpdateDto(
                date,
                (short) 5,
                "Great",
                1L,
                doctorId,
                patientId
        );

        assertEquals(date, dto.getDate());
        assertEquals((short) 5, dto.getScore());
        assertEquals("Great", dto.getComment());
        assertEquals(1L, dto.getHospital_id());
        assertEquals(doctorId, dto.getDoctor_id());
        assertEquals(patientId, dto.getPatient_id());
    }

    @Test
    void testFeedbackViewDto_AllArgsConstructor() {
        LocalDate date = LocalDate.now();
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        FeedbackViewDto dto = new FeedbackViewDto(
                date,
                (short) 4,
                "Good",
                2L,
                doctorId,
                patientId
        );

        assertEquals(date, dto.getDate());
        assertEquals((short) 4, dto.getScore());
        assertEquals("Good", dto.getComment());
        assertEquals(2L, dto.getHospital_id());
        assertEquals(doctorId, dto.getDoctor_id());
        assertEquals(patientId, dto.getPatient_id());
    }
}

