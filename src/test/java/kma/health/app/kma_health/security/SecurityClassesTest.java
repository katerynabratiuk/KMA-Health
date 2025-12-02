package kma.health.app.kma_health.security;

import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.repository.DoctorRepository;
import kma.health.app.kma_health.repository.LabAssistantRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityClassesTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private LabAssistantRepository labAssistantRepository;

    @Test
    void testRepositoryConfig_GetRepositories() {
        RepositoryConfig config = new RepositoryConfig();

        Map<UserRole, ?> repositories = config.authUserRepositories(
                doctorRepository, patientRepository, labAssistantRepository);

        assertNotNull(repositories);
        assertEquals(3, repositories.size());
        assertTrue(repositories.containsKey(UserRole.PATIENT));
        assertTrue(repositories.containsKey(UserRole.DOCTOR));
        assertTrue(repositories.containsKey(UserRole.LAB_ASSISTANT));
    }

    @Test
    void testSchedulingConfig() {
        SchedulingConfig config = new SchedulingConfig();
        // Just verify it can be instantiated
        assertNotNull(config);
    }

    @Test
    void testRestConfig() {
        RestConfig config = new RestConfig();
        var restTemplate = config.restTemplate();
        assertNotNull(restTemplate);
    }
}

