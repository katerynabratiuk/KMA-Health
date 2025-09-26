package kma.health.app.kma_health.enums;

public enum UserRole {
    DOCTOR,
    PATIENT,
    LAB_ASSISTANT;

    public static UserRole fromString(String value) {
        for (UserRole role : UserRole.values())
            if (role.name().equalsIgnoreCase(value))
                return role;
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}

