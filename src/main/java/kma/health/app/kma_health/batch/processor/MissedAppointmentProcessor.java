package kma.health.app.kma_health.batch.processor;

import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.enums.AppointmentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class MissedAppointmentProcessor implements ItemProcessor<Appointment, Appointment> {

    private static final Logger log = LoggerFactory.getLogger(MissedAppointmentProcessor.class);

    @Override
    public Appointment process(Appointment appointment) {
        MDC.put("batchStep", "processor");
        MDC.put("appointmentId", appointment.getId().toString());
        MDC.put("previousStatus", appointment.getStatus().toString());

        log.info("Processing appointment {} - changing status from {} to MISSED",
                appointment.getId(), appointment.getStatus());

        appointment.setStatus(AppointmentStatus.MISSED);

        MDC.put("newStatus", "MISSED");
        MDC.put("referralId", appointment.getReferral().getId().toString());
        log.info("Appointment {} marked as MISSED for patient referral: {}",
                appointment.getId(), appointment.getReferral().getId());

        MDC.remove("batchStep");
        MDC.remove("previousStatus");
        MDC.remove("newStatus");

        return appointment;
    }
}
