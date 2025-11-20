package kma.health.app.kma_health.logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final Map<UUID, List<Long>> storage = new ConcurrentHashMap<>();

    @Around("@annotation(rateLimited)")
    public Object applyRateLimit(ProceedingJoinPoint pjp, RateLimited rateLimited) throws Throwable {
        try {
            Object result = pjp.proceed();
            String userIdStr = MDC.get("userId");

            if (userIdStr != null) {
                UUID userId = UUID.fromString(userIdStr);
                long now = System.currentTimeMillis();
                long windowStart = now - rateLimited.timeWindowSeconds() * 1000L;

                List<Long> calls = storage.computeIfAbsent(userId, k -> new ArrayList<>());
                synchronized (calls) {
                    calls.removeIf(ts -> ts < windowStart);

                    if (calls.size() >= rateLimited.maxCalls()) {
                        throw new RateLimitExceededException(
                                "Rate limit exceeded: only " + rateLimited.maxCalls() +
                                " attempts per " + rateLimited.timeWindowSeconds() +
                                " seconds allowed for user " + userId
                        );
                    }

                    calls.add(now);
                }
            }

            return result;

        } finally {
            MDC.clear();
        }
    }

}
