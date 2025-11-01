package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByReferral_Patient_Id(UUID patientId);

    List<Appointment> findByReferral_Patient_IdAndHospital_Id(UUID patientId, Long hospitalId);

    List<Appointment> findByReferral_Patient_IdAndDoctor_Id(UUID patientId, UUID doctorId);
}

