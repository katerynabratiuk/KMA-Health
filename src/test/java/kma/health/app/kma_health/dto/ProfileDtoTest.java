package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileDtoTest {

    @Test
    void testProfileDto_FromPatient() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setPhoneNumber("+380991234567");
        patient.setProfilePictureUrl("/images/profile.png");

        ProfileDto dto = new ProfileDto(patient);

        assertEquals("John Doe", dto.getFullName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getDateOfBirth());
        assertEquals("+380991234567", dto.getPhoneNumber());
        assertEquals("/images/profile.png", dto.getProfilePictureUrl());
        assertEquals("adult", dto.getPatientType());
    }

    @Test
    void testProfileDto_FromPatient_Child() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("Child Patient");
        patient.setBirthDate(LocalDate.now().minusYears(10));

        ProfileDto dto = new ProfileDto(patient);

        assertEquals("child", dto.getPatientType());
    }

    @Test
    void testProfileDto_FromPatient_NullBirthDate() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("No Birth Date");
        patient.setBirthDate(null);

        ProfileDto dto = new ProfileDto(patient);

        assertEquals("N/A", dto.getPatientType());
    }

    @Test
    void testProfileDto_FromDoctor() {
        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Hospital hospital = new Hospital();
        hospital.setName("City Hospital");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Smith");
        doctor.setEmail("dr.smith@example.com");
        doctor.setBirthDate(LocalDate.of(1980, 5, 15));
        doctor.setPhoneNumber("+380997654321");
        doctor.setProfilePictureUrl("/images/doctor.png");
        doctor.setDoctorType(doctorType);
        doctor.setHospital(hospital);
        doctor.setDescription("Experienced cardiologist");
        doctor.setType("Specialist");

        ProfileDto dto = new ProfileDto(doctor);

        assertEquals("Dr. Smith", dto.getFullName());
        assertEquals("Cardiologist", dto.getDoctorType());
        assertEquals("City Hospital", dto.getHospitalName());
        assertEquals("Experienced cardiologist", dto.getDescription());
        assertEquals("Specialist", dto.getType());
    }

    @Test
    void testProfileDto_FromLabAssistant() {
        Hospital hospital = new Hospital();
        hospital.setName("Lab Hospital");

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(UUID.randomUUID());
        labAssistant.setFullName("Lab Tech");
        labAssistant.setEmail("lab@example.com");
        labAssistant.setBirthDate(LocalDate.of(1985, 3, 20));
        labAssistant.setPhoneNumber("+380993456789");
        labAssistant.setProfilePictureUrl("/images/lab.png");
        labAssistant.setHospital(hospital);

        ProfileDto dto = new ProfileDto(labAssistant);

        assertEquals("Lab Tech", dto.getFullName());
        assertEquals("Lab Hospital", dto.getHospitalName());
    }

    @Test
    void testProfileDto_SettersAndGetters() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("Test");
        
        ProfileDto dto = new ProfileDto(patient);

        dto.setFullName("Test User");
        dto.setEmail("test@example.com");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setPhoneNumber("+380991234567");
        dto.setProfilePictureUrl("/images/test.png");
        dto.setDoctorType("Therapist");
        dto.setHospitalName("Test Hospital");
        dto.setDescription("Test description");
        dto.setFamilyDoctorName("Dr. Family");

        assertEquals("Test User", dto.getFullName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getDateOfBirth());
        assertEquals("+380991234567", dto.getPhoneNumber());
        assertEquals("/images/test.png", dto.getProfilePictureUrl());
        assertEquals("Therapist", dto.getDoctorType());
        assertEquals("Test Hospital", dto.getHospitalName());
        assertEquals("Test description", dto.getDescription());
        assertEquals("Dr. Family", dto.getFamilyDoctorName());
    }
}

