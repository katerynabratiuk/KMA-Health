package kma.health.app.kma_health.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);
    private static final Marker BATCH = MarkerFactory.getMarker("BATCH");

    @Override
    public void beforeJob(JobExecution jobExecution) {
        MDC.put("jobId", String.valueOf(jobExecution.getJobId()));
        MDC.put("jobName", jobExecution.getJobInstance().getJobName());

        log.info(BATCH, "------------------------------------------");
        log.info(BATCH, "STARTING MISSED APPOINTMENT JOB");
        log.info(BATCH, "Job ID: {}", jobExecution.getJobId());
        log.info(BATCH, "Start Time: {}", jobExecution.getStartTime());
        log.info(BATCH, "------------------------------------------");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Duration duration = Duration.between(
                jobExecution.getStartTime(),
                jobExecution.getEndTime());

        MDC.put("jobStatus", jobExecution.getStatus().toString());
        MDC.put("durationMs", String.valueOf(duration.toMillis()));

        log.info(BATCH, "------------------------------------------");
        log.info(BATCH, "MISSED APPOINTMENT JOB COMPLETED");
        log.info(BATCH, "Job ID: {}", jobExecution.getJobId());
        log.info(BATCH, "Status: {}", jobExecution.getStatus());
        log.info(BATCH, "End Time: {}", jobExecution.getEndTime());
        log.info(BATCH, "Duration: {} ms", duration.toMillis());

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            Long totalAppointments = (Long) jobExecution.getExecutionContext().get("totalAppointments");
            Long missedAppointments = (Long) jobExecution.getExecutionContext().get("missedAppointments");

            MDC.put("totalAppointments", String.valueOf(totalAppointments));
            MDC.put("missedAppointments", String.valueOf(missedAppointments));

            log.info(BATCH, "Summary - Total Appointments: {}, Missed: {}",
                    totalAppointments, missedAppointments);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error(BATCH, "Job failed with exceptions:");
            jobExecution.getAllFailureExceptions().forEach(ex -> log.error(BATCH, "Error: {}", ex.getMessage(), ex));
        }

        log.info(BATCH, "------------------------------------------");

        // Clean up MDC
        MDC.clear();
    }
}
