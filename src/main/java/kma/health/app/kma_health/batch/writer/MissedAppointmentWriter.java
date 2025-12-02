package kma.health.app.kma_health.batch.writer;

import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MissedAppointmentWriter implements ItemWriter<Appointment> {

    private static final Logger log = LoggerFactory.getLogger(MissedAppointmentWriter.class);
    private final AppointmentRepository appointmentRepository;

    @Override
    public void write(Chunk<? extends Appointment> chunk) {
        MDC.put("batchStep", "writer");
        MDC.put("chunkSize", String.valueOf(chunk.size()));

        log.info("Writing {} missed appointments to database", chunk.size());

        for (Appointment appointment : chunk) {
            appointmentRepository.save(appointment);
            MDC.put("appointmentId", appointment.getId().toString());
            log.info("Saved appointment {} with status MISSED", appointment.getId());
        }

        log.info("Successfully updated {} appointments", chunk.size());

        MDC.remove("batchStep");
        MDC.remove("chunkSize");
    }
}
