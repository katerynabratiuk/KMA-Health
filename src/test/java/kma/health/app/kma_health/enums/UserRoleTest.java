package kma.health.app.kma_health.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserRoleTest {

    @Test
    public void testFromString_ShouldReturnDoctorRole() {
        assertEquals(UserRole.DOCTOR, UserRole.fromString("DOCTOR"));
        assertEquals(UserRole.DOCTOR, UserRole.fromString("doctor"));
        assertEquals(UserRole.DOCTOR, UserRole.fromString("Doctor"));
    }

    @Test
    public void testFromString_ShouldReturnPatientRole() {
        assertEquals(UserRole.PATIENT, UserRole.fromString("PATIENT"));
        assertEquals(UserRole.PATIENT, UserRole.fromString("patient"));
        assertEquals(UserRole.PATIENT, UserRole.fromString("Patient"));
    }

    @Test
    public void testFromString_ShouldReturnLabAssistantRole() {
        assertEquals(UserRole.LAB_ASSISTANT, UserRole.fromString("LAB_ASSISTANT"));
        assertEquals(UserRole.LAB_ASSISTANT, UserRole.fromString("lab_assistant"));
        assertEquals(UserRole.LAB_ASSISTANT, UserRole.fromString("Lab_Assistant"));
    }

    @Test
    public void testFromString_ShouldThrowExceptionForUnknownRole() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.fromString("UNKNOWN");
        });
    }

    @Test
    public void testFromString_ShouldThrowExceptionForEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.fromString("");
        });
    }

    @Test
    public void testFromString_ShouldThrowExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.fromString(null);
        });
    }

    @Test
    public void testValues_ShouldContainAllRoles() {
        UserRole[] roles = UserRole.values();
        assertEquals(3, roles.length);
        assertTrue(java.util.Arrays.asList(roles).contains(UserRole.DOCTOR));
        assertTrue(java.util.Arrays.asList(roles).contains(UserRole.PATIENT));
        assertTrue(java.util.Arrays.asList(roles).contains(UserRole.LAB_ASSISTANT));
    }

    @Test
    public void testName_ShouldReturnCorrectName() {
        assertEquals("DOCTOR", UserRole.DOCTOR.name());
        assertEquals("PATIENT", UserRole.PATIENT.name());
        assertEquals("LAB_ASSISTANT", UserRole.LAB_ASSISTANT.name());
    }

    @Test
    public void testOrdinal_ShouldReturnCorrectOrdinal() {
        assertEquals(0, UserRole.DOCTOR.ordinal());
        assertEquals(1, UserRole.PATIENT.ordinal());
        assertEquals(2, UserRole.LAB_ASSISTANT.ordinal());
    }
}

