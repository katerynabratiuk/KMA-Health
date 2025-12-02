package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PatientDtoTest {

    @Test
    void testPatientDto_FromEntity() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setPhoneNumber("+380991234567");
        patient.setPassportNumber("AB123456");

        PatientDto dto = new PatientDto(patient);

        assertEquals("John Doe", dto.getFullName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthDate());
        assertEquals("+380991234567", dto.getPhoneNumber());
        assertEquals("AB123456", dto.getPassportNumber());
    }

    @Test
    void testPatientDto_SettersAndGetters() {
        PatientDto dto = new PatientDto();

        dto.setFullName("Jane Doe");
        dto.setEmail("jane@example.com");
        dto.setBirthDate(LocalDate.of(1995, 6, 15));
        dto.setPhoneNumber("+380997654321");
        dto.setPassportNumber("CD789012");

        assertEquals("Jane Doe", dto.getFullName());
        assertEquals("jane@example.com", dto.getEmail());
        assertEquals(LocalDate.of(1995, 6, 15), dto.getBirthDate());
        assertEquals("+380997654321", dto.getPhoneNumber());
        assertEquals("CD789012", dto.getPassportNumber());
    }

    @Test
    void testPatientDto_AllArgsConstructor() {
        PatientDto dto = new PatientDto(
            "AB123456",
            "test@example.com",
            "+380991234567",
            "Test User",
            LocalDate.of(1990, 1, 1)
        );

        assertEquals("AB123456", dto.getPassportNumber());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("+380991234567", dto.getPhoneNumber());
        assertEquals("Test User", dto.getFullName());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getBirthDate());
    }
}

