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
        assertThrows(CoordinatesNotFoundException.class, () -> {
            geocodingService.getCoordinatesByAddress("nonexistent_address_xyz_123456789");
        });
    }

    @Test
    void testGetCoordinatesByAddress_EmptyAddress() {
        assertThrows(CoordinatesNotFoundException.class, () -> {
            geocodingService.getCoordinatesByAddress("");
        });
    }

    @Test
    void testGetCoordinatesByAddress_SpecialCharacters() {
        assertThrows(CoordinatesNotFoundException.class, () -> {
            geocodingService.getCoordinatesByAddress("!@#$%^&*()+={}[]|\\:\";<>?,./");
        });
    }

    @Test
    void testGetCoordinatesByAddress_UnicodeAddress() {
        try {
            HospitalGeocodingService.Coordinates result = 
                geocodingService.getCoordinatesByAddress("вулиця_неіснуюча_xyz12345");
            assertNotNull(result);
        } catch (CoordinatesNotFoundException e) {
            assertTrue(e.getMessage().contains("Couldn't get coordinates") || 
                       e.getMessage().contains("Error while fetching"));
        }
    }

    @Test
    void testGetCoordinatesByAddress_VeryLongAddress() {
        StringBuilder longAddress = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longAddress.append("nonexistent_street_");
        }
        
        assertThrows(CoordinatesNotFoundException.class, () -> {
            geocodingService.getCoordinatesByAddress(longAddress.toString());
        });
    }

    @Test
    void testGetCoordinatesByAddress_NullHandling() {
        assertThrows(CoordinatesNotFoundException.class, () -> {
            geocodingService.getCoordinatesByAddress(null);
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

    @Test
    void testCoordinates_EqualsWithNull() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);

        assertNotEquals(c1, null);
    }

    @Test
    void testCoordinates_EqualsWithSameObject() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);

        assertEquals(c1, c1);
    }

    @Test
    void testCoordinates_EqualsWithDifferentClass() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);

        assertNotEquals(c1, "not a coordinate");
    }

    @Test
    void testCoordinates_DifferentLatitude() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);
        HospitalGeocodingService.Coordinates c2 = 
            new HospitalGeocodingService.Coordinates(51.45, 30.52);

        assertNotEquals(c1, c2);
    }

    @Test
    void testCoordinates_DifferentLongitude() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(50.45, 30.52);
        HospitalGeocodingService.Coordinates c2 = 
            new HospitalGeocodingService.Coordinates(50.45, 31.52);

        assertNotEquals(c1, c2);
    }

    @Test
    void testCoordinates_NegativeValues() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(-50.45, -30.52);

        assertEquals(-50.45, c1.getLatitude());
        assertEquals(-30.52, c1.getLongitude());
    }

    @Test
    void testCoordinates_ZeroValues() {
        HospitalGeocodingService.Coordinates c1 = 
            new HospitalGeocodingService.Coordinates(0.0, 0.0);

        assertEquals(0.0, c1.getLatitude());
        assertEquals(0.0, c1.getLongitude());
    }
}

