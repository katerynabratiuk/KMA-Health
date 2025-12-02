package kma.health.app.kma_health.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimitAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RateLimited rateLimited;

    private RateLimitAspect rateLimitAspect;

    @BeforeEach
    void setUp() {
        rateLimitAspect = new RateLimitAspect();
        MDC.clear();
    }

    @Test
    void testApplyRateLimit_Success() throws Throwable {
        UUID userId = UUID.randomUUID();
        MDC.put("userId", userId.toString());

        when(joinPoint.proceed()).thenReturn("result");
        when(rateLimited.maxCalls()).thenReturn(5);
        when(rateLimited.timeWindowSeconds()).thenReturn(60);

        Object result = rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        assertEquals("result", result);
        verify(joinPoint).proceed();
    }

    @Test
    void testApplyRateLimit_NoUserIdInMDC() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        assertEquals("result", result);
        verify(joinPoint).proceed();
    }

    @Test
    void testApplyRateLimit_ExceedsLimit() throws Throwable {
        UUID userId = UUID.randomUUID();

        when(joinPoint.proceed()).thenReturn("result");
        when(rateLimited.maxCalls()).thenReturn(2);
        when(rateLimited.timeWindowSeconds()).thenReturn(60);

        MDC.put("userId", userId.toString());
        rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        MDC.put("userId", userId.toString());
        rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        MDC.put("userId", userId.toString());

        try {
            rateLimitAspect.applyRateLimit(joinPoint, rateLimited);
            MDC.put("userId", userId.toString());
            assertThrows(RateLimitExceededException.class, () -> {
                rateLimitAspect.applyRateLimit(joinPoint, rateLimited);
            });
        } catch (RateLimitExceededException e) {
        }
    }

    @Test
    void testApplyRateLimit_ResetsAfterTimeWindow() throws Throwable {
        UUID userId = UUID.randomUUID();
        MDC.put("userId", userId.toString());

        when(joinPoint.proceed()).thenReturn("result");
        when(rateLimited.maxCalls()).thenReturn(1);
        when(rateLimited.timeWindowSeconds()).thenReturn(1);

        rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        Thread.sleep(1100);

        MDC.put("userId", userId.toString());

        Object result = rateLimitAspect.applyRateLimit(joinPoint, rateLimited);
        assertEquals("result", result);
    }

    @Test
    void testApplyRateLimit_DifferentUsers() throws Throwable {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        when(joinPoint.proceed()).thenReturn("result");
        when(rateLimited.maxCalls()).thenReturn(1);
        when(rateLimited.timeWindowSeconds()).thenReturn(60);

        MDC.put("userId", userId1.toString());
        rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        MDC.put("userId", userId2.toString());
        Object result = rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        assertEquals("result", result);
    }

    @Test
    void testApplyRateLimit_ClearsMDCOnSuccess() throws Throwable {
        UUID userId = UUID.randomUUID();
        MDC.put("userId", userId.toString());
        MDC.put("otherKey", "otherValue");

        when(joinPoint.proceed()).thenReturn("result");
        when(rateLimited.maxCalls()).thenReturn(5);
        when(rateLimited.timeWindowSeconds()).thenReturn(60);

        rateLimitAspect.applyRateLimit(joinPoint, rateLimited);

        assertNull(MDC.get("userId"));
        assertNull(MDC.get("otherKey"));
    }

    @Test
    void testApplyRateLimit_ClearsMDCOnException() throws Throwable {
        UUID userId = UUID.randomUUID();
        MDC.put("userId", userId.toString());

        when(joinPoint.proceed()).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> {
            rateLimitAspect.applyRateLimit(joinPoint, rateLimited);
        });

        assertNull(MDC.get("userId"));
    }
}

