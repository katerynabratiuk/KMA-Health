package kma.health.app.kma_health.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentStatusTest {

    @Test
    public void testValues_ShouldContainAllStatuses() {
        AppointmentStatus[] statuses = AppointmentStatus.values();
        assertEquals(4, statuses.length);
        assertTrue(java.util.Arrays.asList(statuses).contains(AppointmentStatus.SCHEDULED));
        assertTrue(java.util.Arrays.asList(statuses).contains(AppointmentStatus.OPEN));
        assertTrue(java.util.Arrays.asList(statuses).contains(AppointmentStatus.FINISHED));
        assertTrue(java.util.Arrays.asList(statuses).contains(AppointmentStatus.MISSED));
    }

    @Test
    public void testName_ShouldReturnCorrectName() {
        assertEquals("SCHEDULED", AppointmentStatus.SCHEDULED.name());
        assertEquals("OPEN", AppointmentStatus.OPEN.name());
        assertEquals("FINISHED", AppointmentStatus.FINISHED.name());
        assertEquals("MISSED", AppointmentStatus.MISSED.name());
    }

    @Test
    public void testOrdinal_ShouldReturnCorrectOrdinal() {
        assertEquals(0, AppointmentStatus.SCHEDULED.ordinal());
        assertEquals(1, AppointmentStatus.OPEN.ordinal());
        assertEquals(2, AppointmentStatus.FINISHED.ordinal());
        assertEquals(3, AppointmentStatus.MISSED.ordinal());
    }

    @Test
    public void testValueOf_ShouldReturnCorrectStatus() {
        assertEquals(AppointmentStatus.SCHEDULED, AppointmentStatus.valueOf("SCHEDULED"));
        assertEquals(AppointmentStatus.OPEN, AppointmentStatus.valueOf("OPEN"));
        assertEquals(AppointmentStatus.FINISHED, AppointmentStatus.valueOf("FINISHED"));
        assertEquals(AppointmentStatus.MISSED, AppointmentStatus.valueOf("MISSED"));
    }

    @Test
    public void testValueOf_ShouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            AppointmentStatus.valueOf("INVALID");
        });
    }
}

