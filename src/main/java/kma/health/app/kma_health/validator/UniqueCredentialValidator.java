package kma.health.app.kma_health.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kma.health.app.kma_health.repository.DoctorRepository;
import kma.health.app.kma_health.repository.LabAssistantRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueCredentialValidator implements ConstraintValidator<UniqueCredential, String> {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final LabAssistantRepository labAssistantRepository;

    private String field;

    @Override
    public void initialize(UniqueCredential constraintAnnotation) {
        this.field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        return switch (field) {
            case "email" -> !existsByEmail(value);
            case "passportNumber" -> !existsByPassportNumber(value);
            case "phoneNumber" -> !existsByPhoneNumber(value);
            default -> true;
        };
    }

    private boolean existsByEmail(String email) {
        return patientRepository.findByEmail(email).isPresent()
                || doctorRepository.findByEmail(email).isPresent()
                || labAssistantRepository.findByEmail(email).isPresent();
    }

    private boolean existsByPassportNumber(String passportNumber) {
        return patientRepository.findByPassportNumber(passportNumber).isPresent()
                || doctorRepository.findByPassportNumber(passportNumber).isPresent()
                || labAssistantRepository.findByPassportNumber(passportNumber).isPresent();
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return patientRepository.findByPhoneNumber(phoneNumber).isPresent()
                || doctorRepository.findByPhoneNumber(phoneNumber).isPresent()
                || labAssistantRepository.findByPhoneNumber(phoneNumber).isPresent();
    }
}
