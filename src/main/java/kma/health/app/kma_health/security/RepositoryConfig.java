package kma.health.app.kma_health.security;

import kma.health.app.kma_health.entity.AuthUser;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.repository.AuthUserRepository;
import kma.health.app.kma_health.repository.DoctorRepository;
import kma.health.app.kma_health.repository.LabAssistantRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RepositoryConfig {

    @Bean
    public Map<UserRole, AuthUserRepository<? extends AuthUser>> authUserRepositories(
            DoctorRepository doctorRepository,
            PatientRepository patientRepository,
            LabAssistantRepository labAssistantRepository
    ) {
        return Map.of(
                UserRole.DOCTOR, doctorRepository,
                UserRole.PATIENT, patientRepository,
                UserRole.LAB_ASSISTANT, labAssistantRepository
        );
    }
}
