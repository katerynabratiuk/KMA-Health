package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.enums.HospitalType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalDtoTest {

    @Test
    public void testFromEntity_ShouldMapAllFields() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        hospital.setCity("Kyiv");
        hospital.setType(HospitalType.PUBLIC);

        HospitalDto dto = HospitalDto.fromEntity(hospital);

        assertEquals(1L, dto.getId());
        assertEquals("Test Hospital", dto.getName());
        assertEquals("Test Address", dto.getAddress());
        assertEquals("Kyiv", dto.getCity());
        assertEquals(HospitalType.PUBLIC, dto.getType());
    }

    @Test
    public void testFromEntity_ShouldHandleNullFields() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);

        HospitalDto dto = HospitalDto.fromEntity(hospital);

        assertEquals(1L, dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getAddress());
        assertNull(dto.getCity());
        assertNull(dto.getType());
    }

    @Test
    public void testNoArgsConstructor() {
        HospitalDto dto = new HospitalDto();
        assertNotNull(dto);
    }

    @Test
    public void testAllArgsConstructor() {
        HospitalDto dto = new HospitalDto(1L, "Hospital", "Address", "123456", "City", HospitalType.PUBLIC);
        
        assertEquals(1L, dto.getId());
        assertEquals("Hospital", dto.getName());
        assertEquals("Address", dto.getAddress());
        assertEquals("123456", dto.getPhoneNumber());
        assertEquals("City", dto.getCity());
        assertEquals(HospitalType.PUBLIC, dto.getType());
    }

    @Test
    public void testSettersAndGetters() {
        HospitalDto dto = new HospitalDto();
        
        dto.setId(2L);
        dto.setName("New Hospital");
        dto.setAddress("New Address");
        dto.setPhoneNumber("+380991234567");
        dto.setCity("Lviv");
        dto.setType(HospitalType.PRIVATE);

        assertEquals(2L, dto.getId());
        assertEquals("New Hospital", dto.getName());
        assertEquals("New Address", dto.getAddress());
        assertEquals("+380991234567", dto.getPhoneNumber());
        assertEquals("Lviv", dto.getCity());
        assertEquals(HospitalType.PRIVATE, dto.getType());
    }

    @Test
    public void testEquals() {
        HospitalDto dto1 = new HospitalDto(1L, "Hospital", "Address", "123456", "City", HospitalType.PUBLIC);
        HospitalDto dto2 = new HospitalDto(1L, "Hospital", "Address", "123456", "City", HospitalType.PUBLIC);
        HospitalDto dto3 = new HospitalDto(2L, "Hospital", "Address", "123456", "City", HospitalType.PUBLIC);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    public void testHashCode() {
        HospitalDto dto1 = new HospitalDto(1L, "Hospital", "Address", "123456", "City", HospitalType.PUBLIC);
        HospitalDto dto2 = new HospitalDto(1L, "Hospital", "Address", "123456", "City", HospitalType.PUBLIC);

        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    public void testToString() {
        HospitalDto dto = new HospitalDto(1L, "Hospital", "Address", "123456", "City", HospitalType.PUBLIC);
        String str = dto.toString();

        assertNotNull(str);
        assertTrue(str.contains("Hospital"));
    }
}

