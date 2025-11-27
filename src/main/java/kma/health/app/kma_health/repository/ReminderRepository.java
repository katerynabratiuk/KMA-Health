package kma.health.app.kma_health.repository;

import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, UUID> {
    List<Reminder> findByPatientAndReminderDate(Patient patient, LocalDate reminderDate);

    List<Reminder> findByReminderDateBefore(LocalDate date);
}

