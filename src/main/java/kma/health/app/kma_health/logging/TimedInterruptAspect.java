package kma.health.app.kma_health.logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Aspect
@Component
@RequiredArgsConstructor
public class TimedInterruptAspect {

    private final ExecutorService executor =
            Executors.newCachedThreadPool();

    @Around("@annotation(timed)")
    public Object around(ProceedingJoinPoint pjp, TimedInterruptible timed) throws Throwable {
        long timeout = timed.timeout();

        Callable<Object> task = () -> {
            try {
                return pjp.proceed();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Method interrupted", e);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        Future<Object> future = executor.submit(task);

        try {
            long start = System.currentTimeMillis();
            Object result = future.get(timeout, TimeUnit.MILLISECONDS);
            long end = System.currentTimeMillis();

            System.out.println(
                    pjp.getSignature() + " executed in " + (end - start) + " ms"
            );

            return result;

        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("Method exceeded timeout: " + timeout + " ms");
        }
    }
}

