package kma.health.app.kma_health.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kma.health.app.kma_health.dto.DoctorRegisterRequest;

import java.time.LocalDate;

public class BirthBeforeWorkValidator implements ConstraintValidator<BirthBeforeWork, DoctorRegisterRequest> {

    @Override
    public boolean isValid(DoctorRegisterRequest value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        LocalDate birthDate = value.getBirthDate();
        LocalDate startedWorking = value.getStartedWorking();

        if (birthDate == null || startedWorking == null) {
            return true;
        }

        boolean valid = birthDate.isBefore(startedWorking);
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("startedWorking")
                    .addConstraintViolation();
        }

        return valid;
    }
}

