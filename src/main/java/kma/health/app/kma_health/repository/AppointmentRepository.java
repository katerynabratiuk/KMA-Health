package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByReferral_Patient_Id(UUID patientId);

    List<Appointment> findByReferral_Patient_IdAndHospital_Id(UUID patientId, Long hospitalId);

    List<Appointment> findByReferral_Patient_IdAndDoctor_Id(UUID patientId, UUID doctorId);

    List<Appointment> findByDoctor_Id(UUID doctorId);

    List<Appointment> findByDoctor_IdAndReferral_Patient_Id(UUID doctorId, UUID patientId);

    List<Appointment> findByReferral_Patient_idAndDateBetween(UUID patientId, LocalDate start, LocalDate end);

    List<Appointment> findByDoctor_IdAndDateBetween(UUID doctorId, LocalDate start, LocalDate end);

    List<Appointment> findByStatus(AppointmentStatus status);
}

