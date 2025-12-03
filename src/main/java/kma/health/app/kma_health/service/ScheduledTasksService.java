package kma.health.app.kma_health.service;

import jakarta.transaction.Transactional;
import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.entity.Reminder;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduledTasksService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasksService.class);

    private final AppointmentRepository appointmentRepository;
    private final ReminderRepository reminderRepository;

    // @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void generateAppointmentReminders() {
        log.info("Starting daily appointment reminder generation");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Appointment> upcomingAppointments = appointmentRepository
                .findByStatus(AppointmentStatus.SCHEDULED)
                .stream()
                .filter(a -> a.getDate().equals(today) || a.getDate().equals(tomorrow))
                .toList();

        int remindersCreated = 0;

        for (Appointment appointment : upcomingAppointments) {
            Patient patient = appointment.getReferral().getPatient();

            List<Reminder> existingReminders = reminderRepository
                    .findByPatientAndReminderDate(patient, today);

            boolean reminderExists = existingReminders.stream()
                    .anyMatch(r -> r.getText().contains(appointment.getId().toString()));

            if (!reminderExists) {
                Reminder reminder = new Reminder();
                reminder.setId(UUID.randomUUID());
                reminder.setPatient(patient);
                reminder.setReminderDate(today);

                String targetInfo = appointment.getDoctor() != null
                        ? appointment.getDoctor().getFullName()
                        : appointment.getHospital().getName();

                reminder.setText(String.format(
                        "Нагадування: запис %s на %s о %s. %s",
                        appointment.getId(),
                        appointment.getDate(),
                        appointment.getTime(),
                        targetInfo
                ));

                reminderRepository.save(reminder);
                remindersCreated++;
            }
        }

        log.info("Appointment reminder generation completed. Created {} reminders", remindersCreated);
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void markMissedAppointments() {
        log.info("Starting missed appointments check");

        LocalDateTime threshold = LocalDateTime.now().minusHours(2);

        List<Appointment> openAppointments = appointmentRepository.findByStatus(AppointmentStatus.OPEN);

        int missedCount = 0;

        for (Appointment appointment : openAppointments) {
            LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getDate(), appointment.getTime());

            if (appointmentDateTime.isBefore(threshold)) {
                appointment.setStatus(AppointmentStatus.MISSED);
                missedCount++;
            }
        }

        if (missedCount > 0) {
            appointmentRepository.saveAll(openAppointments);
        }

        log.info("Missed appointments check completed. Marked {} appointments as MISSED", missedCount);
    }
}

