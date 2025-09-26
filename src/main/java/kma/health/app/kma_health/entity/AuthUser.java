package kma.health.app.kma_health.entity;

import kma.health.app.kma_health.enums.UserRole;

public interface AuthUser {
    String getEmail();
    void setEmail(String email);
    String getPhoneNumber();
    void setPhoneNumber(String phone);
    String getPassportNumber();
    void setPassportNumber(String passport);
    String getPassword();
    void setPassword(String password);
    UserRole getRole();
}

