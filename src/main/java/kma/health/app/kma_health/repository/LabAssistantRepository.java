package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.LabAssistant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LabAssistantRepository extends JpaRepository<LabAssistant, String> {
    Optional<LabAssistant> findByEmail(String email);
    Optional<LabAssistant> findByPhoneNumber(String phoneNumber);
    Optional<LabAssistant> findByPassportNumber(String passportNumber);
}
