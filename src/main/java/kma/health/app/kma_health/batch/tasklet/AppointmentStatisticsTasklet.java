package kma.health.app.kma_health.batch.tasklet;

import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentStatisticsTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(AppointmentStatisticsTasklet.class);
    private final AppointmentRepository appointmentRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        MDC.put("batchStep", "statistics");

        log.info("------------- APPOINTMENT STATISTICS REPORT -------------");

        long scheduledCount = appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED).size();
        long openCount = appointmentRepository.findByStatus(AppointmentStatus.OPEN).size();
        long missedCount = appointmentRepository.findByStatus(AppointmentStatus.MISSED).size();
        long finishedCount = appointmentRepository.findByStatus(AppointmentStatus.FINISHED).size();
        long totalCount = appointmentRepository.count();

        MDC.put("totalAppointments", String.valueOf(totalCount));
        MDC.put("scheduledCount", String.valueOf(scheduledCount));
        MDC.put("missedCount", String.valueOf(missedCount));

        log.info("Total appointments: {}", totalCount);
        log.info("SCHEDULED: {}", scheduledCount);
        log.info("OPEN: {}", openCount);
        log.info("MISSED: {}", missedCount);
        log.info("FINISHED: {}", finishedCount);
        log.info("------------------------------------------------------");

        chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("totalAppointments", totalCount);
        chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .put("missedAppointments", missedCount);

        MDC.remove("batchStep");

        return RepeatStatus.FINISHED;
    }
}
