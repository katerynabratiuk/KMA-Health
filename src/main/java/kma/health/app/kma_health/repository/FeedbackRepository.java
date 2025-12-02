package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByDoctor_Id(UUID id);

    List<Feedback> findByHospital_Id(Long id);

    Optional<Feedback> findByDoctor_IdAndPatient_Id(UUID doctorId, UUID patientId);

    Optional<Feedback> findByHospital_IdAndPatient_Id(Long hospitalId, UUID patientId);
}
