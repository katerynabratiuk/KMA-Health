package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.entity.MedicalFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface MedicalFileRepository extends JpaRepository<MedicalFile, UUID> {
    Set<MedicalFile> findAllByAppointment_Id(UUID appointmentId);
}
