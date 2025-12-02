package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Hospital;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalFormDtoTest {

    @Test
    void testHospitalFormDto_RecordAccessors() {
        HospitalFormDto dto = new HospitalFormDto(1L, "City Hospital");

        assertEquals(1L, dto.id());
        assertEquals("City Hospital", dto.name());
    }

    @Test
    void testHospitalFormDto_FromEntity() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("City Hospital");

        HospitalFormDto dto = HospitalFormDto.fromEntity(hospital);

        assertEquals(1L, dto.id());
        assertEquals("City Hospital", dto.name());
    }

    @Test
    void testHospitalFormDto_Equals() {
        HospitalFormDto dto1 = new HospitalFormDto(1L, "Hospital");
        HospitalFormDto dto2 = new HospitalFormDto(1L, "Hospital");
        HospitalFormDto dto3 = new HospitalFormDto(2L, "Hospital");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testHospitalFormDto_HashCode() {
        HospitalFormDto dto1 = new HospitalFormDto(1L, "Hospital");
        HospitalFormDto dto2 = new HospitalFormDto(1L, "Hospital");

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testHospitalFormDto_ToString() {
        HospitalFormDto dto = new HospitalFormDto(1L, "Hospital");
        String str = dto.toString();

        assertTrue(str.contains("Hospital"));
    }
}

