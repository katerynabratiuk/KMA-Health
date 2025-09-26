package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}
