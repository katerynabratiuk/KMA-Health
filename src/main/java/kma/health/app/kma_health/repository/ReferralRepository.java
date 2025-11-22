package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReferralRepository extends JpaRepository<Referral, UUID> {
    List<Referral> findByPatientId(UUID patientId);
}
