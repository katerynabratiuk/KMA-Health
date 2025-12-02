package kma.health.app.kma_health.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalTypeTest {

    @Test
    public void testValues_ShouldContainAllTypes() {
        HospitalType[] types = HospitalType.values();
        assertTrue(types.length > 0);
    }

    @Test
    public void testValueOf_ShouldReturnCorrectType() {
        for (HospitalType type : HospitalType.values()) {
            assertEquals(type, HospitalType.valueOf(type.name()));
        }
    }

    @Test
    public void testValueOf_ShouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            HospitalType.valueOf("INVALID_TYPE");
        });
    }

    @Test
    public void testOrdinal_ShouldReturnNonNegative() {
        for (HospitalType type : HospitalType.values()) {
            assertTrue(type.ordinal() >= 0);
        }
    }
}

