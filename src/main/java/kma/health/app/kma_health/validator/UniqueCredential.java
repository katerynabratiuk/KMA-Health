package kma.health.app.kma_health.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueCredentialValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UniqueCredential {
    String message() default "This credential already exists";

    String field();
    String role();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
