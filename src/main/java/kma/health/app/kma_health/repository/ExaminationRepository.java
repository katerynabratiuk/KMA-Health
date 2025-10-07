package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {
}
