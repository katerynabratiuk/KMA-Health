package kma.health.app.kma_health.batch.job;

import kma.health.app.kma_health.batch.listener.JobCompletionNotificationListener;
import kma.health.app.kma_health.batch.processor.MissedAppointmentProcessor;
import kma.health.app.kma_health.batch.reader.MissedAppointmentReader;
import kma.health.app.kma_health.batch.tasklet.AppointmentStatisticsTasklet;
import kma.health.app.kma_health.batch.writer.MissedAppointmentWriter;
import kma.health.app.kma_health.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class MissedAppointmentJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MissedAppointmentReader missedAppointmentReader;
    private final MissedAppointmentProcessor missedAppointmentProcessor;
    private final MissedAppointmentWriter missedAppointmentWriter;
    private final AppointmentStatisticsTasklet appointmentStatisticsTasklet;
    private final JobCompletionNotificationListener jobCompletionListener;

    @Bean
    public Job missedAppointmentJob() {
        return new JobBuilder("missedAppointmentJob", jobRepository)
                .listener(jobCompletionListener)
                .start(updateMissedAppointmentsStep())
                .next(generateStatisticsStep())
                .build();
    }

    @Bean
    public Step updateMissedAppointmentsStep() {
        return new StepBuilder("updateMissedAppointmentsStep", jobRepository)
                .<Appointment, Appointment>chunk(10, transactionManager)
                .reader(missedAppointmentReader)
                .processor(missedAppointmentProcessor)
                .writer(missedAppointmentWriter)
                .build();
    }

    @Bean
    public Step generateStatisticsStep() {
        return new StepBuilder("generateStatisticsStep", jobRepository)
                .tasklet(appointmentStatisticsTasklet, transactionManager)
                .build();
    }
}
