package kma.health.app.kma_health.logging;

import kma.health.app.kma_health.dto.DoctorRegisterRequest;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.service.SlackNotificationService;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvalidRegisterKeyAspectTest {

    @Mock
    private SlackNotificationService slackService;

    @Mock
    private JoinPoint joinPoint;

    @InjectMocks
    private InvalidRegisterKeyAspect aspect;

    @Test
    void testHandleInvalidKey_WithInvalidKeyException() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setEmail("test@example.com");
        request.setRole(UserRole.DOCTOR);

        when(joinPoint.getArgs()).thenReturn(new Object[]{request});

        RuntimeException ex = new RuntimeException("Invalid register key");
        aspect.handleInvalidKey(joinPoint, ex);

        verify(slackService).sendMessage(contains("test@example.com"));
        verify(slackService).sendMessage(contains("DOCTOR"));
    }

    @Test
    void testHandleInvalidKey_WithOtherException() {
        RuntimeException ex = new RuntimeException("Some other error");
        aspect.handleInvalidKey(joinPoint, ex);

        verify(slackService, never()).sendMessage(anyString());
    }

    @Test
    void testHandleInvalidKey_NoRequestInArgs() {
        when(joinPoint.getArgs()).thenReturn(new Object[]{"string", 123});

        RuntimeException ex = new RuntimeException("Invalid register key");
        aspect.handleInvalidKey(joinPoint, ex);

        verify(slackService).sendMessage(contains("request unknown"));
    }

    @Test
    void testHandleInvalidKey_EmptyArgs() {
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        RuntimeException ex = new RuntimeException("Invalid register key");
        aspect.handleInvalidKey(joinPoint, ex);

        verify(slackService).sendMessage(contains("request unknown"));
    }

    @Test
    void testHandleInvalidKey_NullArgs() {
        when(joinPoint.getArgs()).thenReturn(new Object[]{null, null});

        RuntimeException ex = new RuntimeException("Invalid register key");
        aspect.handleInvalidKey(joinPoint, ex);

        verify(slackService).sendMessage(contains("request unknown"));
    }
}

