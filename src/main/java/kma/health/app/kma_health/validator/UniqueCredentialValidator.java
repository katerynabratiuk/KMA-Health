package kma.health.app.kma_health.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kma.health.app.kma_health.enums.UserRole;
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
    private UserRole role;

    @Override
    public void initialize(UniqueCredential constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.role = UserRole.fromString(constraintAnnotation.role());
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
        return switch (role) {
            case UserRole.PATIENT -> patientRepository.findByEmail(email).isPresent();
            case UserRole.DOCTOR -> doctorRepository.findByEmail(email).isPresent();
            case UserRole.LAB_ASSISTANT -> labAssistantRepository.findByEmail(email).isPresent();
        };
    }

    private boolean existsByPassportNumber(String passportNumber) {
        return switch (role) {
            case UserRole.PATIENT -> patientRepository.findByPassportNumber(passportNumber).isPresent();
            case UserRole.DOCTOR -> doctorRepository.findByPassportNumber(passportNumber).isPresent();
            case UserRole.LAB_ASSISTANT -> labAssistantRepository.findByPassportNumber(passportNumber).isPresent();
        };
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return switch (role) {
            case UserRole.PATIENT -> patientRepository.findByPhoneNumber(phoneNumber).isPresent();
            case UserRole.DOCTOR -> doctorRepository.findByPhoneNumber(phoneNumber).isPresent();
            case UserRole.LAB_ASSISTANT -> labAssistantRepository.findByPhoneNumber(phoneNumber).isPresent();
        };
    }
}
