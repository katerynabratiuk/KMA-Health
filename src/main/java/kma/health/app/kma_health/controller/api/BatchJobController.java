package kma.health.app.kma_health.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Tag(name = "Batch Jobs", description = "Endpoints for manual batch job execution")
public class BatchJobController {

    private static final Logger log = LoggerFactory.getLogger(BatchJobController.class);
    private static final Marker BATCH = MarkerFactory.getMarker("BATCH");

    private final JobLauncher jobLauncher;
    private final Job missedAppointmentJob;

    @PostMapping("/missed-appointments/run")
    @PreAuthorize("hasAnyRole('DOCTOR', 'LAB_ASSISTANT', 'ADMIN')")
    @Operation(summary = "Manually trigger missed appointment job", description = "Processes all SCHEDULED appointments where date/time has passed and marks them as MISSED. "
            +
            "Generates statistics report after processing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job executed successfully"),
            @ApiResponse(responseCode = "500", description = "Job execution failed")
    })
    public ResponseEntity<?> runMissedAppointmentJob() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MDC.put("trigger", "manual");
        MDC.put("triggeredBy", auth != null ? auth.getName() : "unknown");

        try {
            log.info(BATCH, "Manual execution of Missed Appointment Job requested");

            // Unique job parameters to allow multiple runs
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("trigger", "manual")
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(missedAppointmentJob, jobParameters);

            MDC.put("jobId", String.valueOf(execution.getJobId()));
            MDC.put("jobStatus", execution.getStatus().toString());
            log.info(BATCH, "Manual job execution completed with status: {}", execution.getStatus());

            return ResponseEntity.ok(Map.of(
                    "message", "Missed appointment job executed successfully",
                    "jobId", execution.getJobId(),
                    "status", execution.getStatus().toString(),
                    "startTime", Objects.requireNonNull(execution.getStartTime()),
                    "endTime", Objects.requireNonNull(execution.getEndTime())));
        } catch (Exception e) {
            MDC.put("error", e.getMessage());
            log.error(BATCH, "Failed to run Missed Appointment Job", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error", "Failed to execute job",
                            "message", e.getMessage()));
        } finally {
            MDC.clear();
        }
    }
}
