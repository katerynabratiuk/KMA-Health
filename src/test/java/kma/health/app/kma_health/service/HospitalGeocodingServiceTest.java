package kma.health.app.kma_health.service;

import kma.health.app.kma_health.exception.CoordinatesNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalGeocodingServiceTest {

    private HospitalGeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        geocodingService = new HospitalGeocodingService();
    }

    @Test
    void testGetCoordinatesByAddress_InvalidAddress() {
        // This will fail to get coordinates from the API
        // and should throw CoordinatesNotFoundException
        assertThrows(CoordinatesNotFoundException.class, () -> {
            geocodingService.getCoordinatesByAddress("nonexistent_address_xyz_123456789");
        });
    }

    @Test
    void testCoordinates_GettersAndSetters() {
        HospitalGeocodingService.Coordinates coordinates = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);

        assertEquals(50.45, coordinates.getLatitude());
        assertEquals(30.52, coordinates.getLongitude());

        coordinates.setLatitude(51.0);
        coordinates.setLongitude(31.0);

        assertEquals(51.0, coordinates.getLatitude());
        assertEquals(31.0, coordinates.getLongitude());
    }

    @Test
    void testCoordinates_Equals() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);
        HospitalGeocodingService.Coordinates c2 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);
        HospitalGeocodingService.Coordinates c3 = 
            new HospitalGeocodingService.Coordinates(51.0, 31.0);

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
    }

    @Test
    void testCoordinates_HashCode() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);
        HospitalGeocodingService.Coordinates c2 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testCoordinates_ToString() {
        HospitalGeocodingService.Coordinates coordinates = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);

        String str = coordinates.toString();
        assertTrue(str.contains("50.45"));
        assertTrue(str.contains("30.52"));
    }
}

