package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.LabAssistant;

import java.util.Optional;

public interface LabAssistantRepository extends AuthUserRepository<LabAssistant> {
    Optional<LabAssistant> findByEmail(String email);
    Optional<LabAssistant> findByPhoneNumber(String phoneNumber);
    Optional<LabAssistant> findByPassportNumber(String passportNumber);
}
