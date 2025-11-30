package kma.health.app.kma_health.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthBeforeWorkValidator.class)
public @interface BirthBeforeWork {

    String message() default "Дата початку роботи має бути пізніше за дату народження";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

