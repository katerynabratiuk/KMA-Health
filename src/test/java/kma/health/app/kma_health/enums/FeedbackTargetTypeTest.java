package kma.health.app.kma_health.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FeedbackTargetTypeTest {

    @Test
    public void testValues_ShouldContainAllTypes() {
        FeedbackTargetType[] types = FeedbackTargetType.values();
        assertTrue(types.length > 0);
    }

    @Test
    public void testValueOf_ShouldReturnCorrectType() {
        for (FeedbackTargetType type : FeedbackTargetType.values()) {
            assertEquals(type, FeedbackTargetType.valueOf(type.name()));
        }
    }

    @Test
    public void testValueOf_ShouldThrowExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            FeedbackTargetType.valueOf("INVALID_TYPE");
        });
    }

    @Test
    public void testOrdinal_ShouldReturnNonNegative() {
        for (FeedbackTargetType type : FeedbackTargetType.values()) {
            assertTrue(type.ordinal() >= 0);
        }
    }
}

