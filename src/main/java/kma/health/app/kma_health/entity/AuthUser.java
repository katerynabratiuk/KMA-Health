package kma.health.app.kma_health.entity;

import kma.health.app.kma_health.enums.UserRole;

import java.time.LocalDate;
import java.util.UUID;

public interface AuthUser {
    String getEmail();
    void setEmail(String email);
    String getPhoneNumber();
    void setPhoneNumber(String phone);
    UUID getId();
    void setId(UUID id);
    String getPassportNumber();
    void setPassportNumber(String passport);
    String getPassword();
    void setPassword(String password);
    UserRole getRole();
    String getFullName();
    void setFullName(String fullName);
    LocalDate getBirthDate();
    void setBirthDate(LocalDate birthDate);
    String getProfilePictureUrl();
    void setProfilePictureUrl(String profilePictureUrl);
}

