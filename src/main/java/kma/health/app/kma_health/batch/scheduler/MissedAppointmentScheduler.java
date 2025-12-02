package kma.health.app.kma_health.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MissedAppointmentScheduler {

    private static final Logger log = LoggerFactory.getLogger(MissedAppointmentScheduler.class);
    private static final Marker BATCH = MarkerFactory.getMarker("BATCH");

    private final JobLauncher jobLauncher;
    private final Job missedAppointmentJob;

    @Scheduled(cron = "0 0 * * * *") // Кожну годину на початку години
    public void runMissedAppointmentJob() {
        MDC.put("trigger", "scheduled");
        MDC.put("executionTime", String.valueOf(System.currentTimeMillis()));

        try {
            log.info(BATCH, "Scheduled execution of Missed Appointment Job triggered");

            // Unique job parameters to allow multiple runs
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("trigger", "scheduled")
                    .toJobParameters();

            jobLauncher.run(missedAppointmentJob, jobParameters);

            log.info(BATCH, "Scheduled job execution completed successfully");
        } catch (Exception e) {
            MDC.put("error", e.getMessage());
            log.error(BATCH, "Failed to run scheduled Missed Appointment Job", e);
        } finally {
            MDC.clear();
        }
    }
}
