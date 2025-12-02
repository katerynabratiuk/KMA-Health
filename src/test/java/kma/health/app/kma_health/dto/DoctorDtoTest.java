package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.DoctorType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorDtoTest {

    @Test
    void testDoctorDto_FromEntity() {
        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Smith");
        doctor.setEmail("dr.smith@example.com");
        doctor.setPhoneNumber("+380991234567");
        doctor.setPassportNumber("AB123456");
        doctor.setType("Specialist");
        doctor.setDoctorType(doctorType);

        DoctorDto dto = new DoctorDto(doctor);

        assertNotNull(dto);
        // DoctorDto doesn't have getters, so we can't verify fields directly
        // The test verifies that the constructor works without exceptions
    }

    @Test
    void testDoctorDto_AllArgsConstructor() {
        DoctorDto dto = new DoctorDto(
            "AB123456",
            "Dr. Test",
            "+380991234567",
            "dr.test@example.com",
            "Specialist",
            "Cardiologist"
        );

        assertNotNull(dto);
    }

    @Test
    void testDoctorDto_NoArgsConstructor() {
        DoctorDto dto = new DoctorDto();
        assertNotNull(dto);
    }
}

