package kma.health.app.kma_health.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorSearchDtoTest {

    @Test
    void testDoctorSearchDto_SettersAndGetters() {
        DoctorSearchDto dto = new DoctorSearchDto();

        dto.setQuery("Dr. Smith");
        dto.setCity("Kyiv");
        dto.setDoctorType("Cardiologist");
        dto.setHospitalId(1L);

        DoctorSearchDto.SortBy sortBy = new DoctorSearchDto.SortBy("rating", "dsc");
        dto.setSortBy(sortBy);

        assertEquals("Dr. Smith", dto.getQuery());
        assertEquals("Kyiv", dto.getCity());
        assertEquals("Cardiologist", dto.getDoctorType());
        assertEquals(1L, dto.getHospitalId());
        assertEquals("rating", dto.getSortBy().getParam());
        assertEquals("dsc", dto.getSortBy().getDirection());
    }

    @Test
    void testSortBy_SettersAndGetters() {
        DoctorSearchDto.SortBy sortBy = new DoctorSearchDto.SortBy();

        sortBy.setParam("distance");
        sortBy.setDirection("asc");

        assertEquals("distance", sortBy.getParam());
        assertEquals("asc", sortBy.getDirection());
    }

    @Test
    void testSortBy_AllArgsConstructor() {
        DoctorSearchDto.SortBy sortBy = new DoctorSearchDto.SortBy("rating", "dsc");

        assertEquals("rating", sortBy.getParam());
        assertEquals("dsc", sortBy.getDirection());
    }
}

