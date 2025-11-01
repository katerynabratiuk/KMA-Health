package kma.health.app.kma_health.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class FeedbackNotPermitted extends RuntimeException{
    public FeedbackNotPermitted(String msg) { super(msg); }
}
