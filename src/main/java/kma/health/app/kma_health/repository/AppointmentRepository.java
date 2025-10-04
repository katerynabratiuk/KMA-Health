package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findAllByReferralPatientPassportNumber(String patientId);
}
