package kma.health.app.kma_health.batch.reader;

import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MissedAppointmentReader implements ItemReader<Appointment> {

    private static final Logger log = LoggerFactory.getLogger(MissedAppointmentReader.class);
    private final AppointmentRepository appointmentRepository;
    private Iterator<Appointment> appointmentIterator;
    private boolean isInitialized = false;

    @Override
    public Appointment read() {
        if (!isInitialized) {
            initializeData();
            isInitialized = true;
        }

        if (appointmentIterator != null && appointmentIterator.hasNext()) {
            Appointment appointment = appointmentIterator.next();
            MDC.put("appointmentId", appointment.getId().toString());
            MDC.put("appointmentDate", appointment.getDate().toString());
            log.info("Reading appointment: {} scheduled for {} at {}",
                    appointment.getId(), appointment.getDate(), appointment.getTime());
            return appointment;
        } else {
            isInitialized = false;
            return null;
        }
    }

    private void initializeData() {
        MDC.put("batchStep", "reader-initialization");
        log.info("Initializing missed appointments reader...");

        // Find all SCHEDULED appointments
        List<Appointment> scheduledAppointments = appointmentRepository
                .findByStatus(AppointmentStatus.SCHEDULED);

        // Filter appointments where date/time has passed
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        List<Appointment> missedAppointments = scheduledAppointments.stream()
                .filter(appointment -> {
                    LocalDate appointmentDate = appointment.getDate();
                    LocalTime appointmentTime = appointment.getTime();

                    // Appointment is missed if and only if:
                    // a) Date is before today
                    // b) Date is today AND time has passed
                    return appointmentDate.isBefore(today) ||
                            (appointmentDate.isEqual(today) && appointmentTime.isBefore(currentTime));
                })
                .toList();

        MDC.put("missedCount", String.valueOf(missedAppointments.size()));
        log.info("Found {} missed appointments to process", missedAppointments.size());
        this.appointmentIterator = missedAppointments.iterator();
        MDC.remove("batchStep");
    }
}
