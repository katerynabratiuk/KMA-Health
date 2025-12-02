package kma.health.app.kma_health.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SearchFormDtoTest {

    @Test
    void testSearchFormDto_SettersAndGetters() {
        SearchFormDto dto = new SearchFormDto();

        dto.setQuery("search query");
        dto.setCity("Kyiv");
        dto.setDoctorType("Cardiologist");
        dto.setSearchType("doctor");
        dto.setSort("rating-asc");
        dto.setUserLat(50.45);
        dto.setUserLon(30.52);

        assertEquals("search query", dto.getQuery());
        assertEquals("Kyiv", dto.getCity());
        assertEquals("Cardiologist", dto.getDoctorType());
        assertEquals("doctor", dto.getSearchType());
        assertEquals("rating-asc", dto.getSort());
        assertEquals(50.45, dto.getUserLat());
        assertEquals(30.52, dto.getUserLon());
    }

    @Test
    void testSearchFormDto_DefaultValues() {
        SearchFormDto dto = new SearchFormDto();

        assertEquals("doctor", dto.getSearchType());
        assertEquals("rating-asc", dto.getSort());
        assertEquals(0, dto.getUserLat());
        assertEquals(0, dto.getUserLon());
    }
}

