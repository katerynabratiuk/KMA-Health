package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.DoctorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorTypeRepository extends JpaRepository<DoctorType, Long> {
    Optional<DoctorType> findByTypeName(String typeName);
}
