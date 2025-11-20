package kma.health.app.kma_health.logging;

import kma.health.app.kma_health.dto.RegisterRequest;
import kma.health.app.kma_health.service.SlackNotificationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class InvalidRegisterKeyAspect {
    private final SlackNotificationService slackService;

    @AfterThrowing(
            pointcut = "@annotation(NotifyInvalidKey)",
            throwing = "ex"
    )
    public void handleInvalidKey(JoinPoint joinPoint, RuntimeException ex) {
        if (!ex.getMessage().contains("Invalid register key"))
            return;

        Object[] args = joinPoint.getArgs();
        RegisterRequest request = null;
        for (Object arg : args) {
            if (arg instanceof RegisterRequest rr) {
                request = rr;
                break;
            }
        }

        if (request == null) {
            slackService.sendMessage("⚠️ Invalid register key used, but request unknown.");
            return;
        }

        String msg = String.format(
                "*Invalid registration key attempt!*\n" +
                "> Email: `%s`\n" +
                "> Role: `%s`\n",
                request.getEmail(),
                request.getRole()
        );
        slackService.sendMessage(msg);
    }
}

