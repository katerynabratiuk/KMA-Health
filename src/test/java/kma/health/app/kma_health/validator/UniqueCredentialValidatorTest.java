package kma.health.app.kma_health.validator;

import jakarta.validation.ConstraintValidatorContext;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.LabAssistant;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.repository.DoctorRepository;
import kma.health.app.kma_health.repository.LabAssistantRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UniqueCredentialValidatorTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private LabAssistantRepository labAssistantRepository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private UniqueCredential annotation;

    @InjectMocks
    private UniqueCredentialValidator validator;

    @Test
    public void testIsValid_ShouldReturnTrueForNullValue() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        assertTrue(validator.isValid(null, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueForEmptyValue() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        assertTrue(validator.isValid("", context));
    }

    @Test
    public void testIsValid_Email_Patient_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        when(patientRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("test@example.com", context));
    }

    @Test
    public void testIsValid_Email_Patient_ShouldReturnFalseWhenExists() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        when(patientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Patient()));

        assertFalse(validator.isValid("test@example.com", context));
    }

    @Test
    public void testIsValid_Email_Doctor_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("DOCTOR");
        validator.initialize(annotation);

        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("doctor@example.com", context));
    }

    @Test
    public void testIsValid_Email_Doctor_ShouldReturnFalseWhenExists() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("DOCTOR");
        validator.initialize(annotation);

        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(new Doctor()));

        assertFalse(validator.isValid("doctor@example.com", context));
    }

    @Test
    public void testIsValid_Email_LabAssistant_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("LAB_ASSISTANT");
        validator.initialize(annotation);

        when(labAssistantRepository.findByEmail("lab@example.com")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("lab@example.com", context));
    }

    @Test
    public void testIsValid_Email_LabAssistant_ShouldReturnFalseWhenExists() {
        when(annotation.field()).thenReturn("email");
        when(annotation.role()).thenReturn("LAB_ASSISTANT");
        validator.initialize(annotation);

        when(labAssistantRepository.findByEmail("lab@example.com")).thenReturn(Optional.of(new LabAssistant()));

        assertFalse(validator.isValid("lab@example.com", context));
    }

    @Test
    public void testIsValid_PassportNumber_Patient_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("passportNumber");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        when(patientRepository.findByPassportNumber("AB123456")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("AB123456", context));
    }

    @Test
    public void testIsValid_PassportNumber_Patient_ShouldReturnFalseWhenExists() {
        when(annotation.field()).thenReturn("passportNumber");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        when(patientRepository.findByPassportNumber("AB123456")).thenReturn(Optional.of(new Patient()));

        assertFalse(validator.isValid("AB123456", context));
    }

    @Test
    public void testIsValid_PhoneNumber_Patient_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("phoneNumber");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        when(patientRepository.findByPhoneNumber("+380991234567")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("+380991234567", context));
    }

    @Test
    public void testIsValid_PhoneNumber_Patient_ShouldReturnFalseWhenExists() {
        when(annotation.field()).thenReturn("phoneNumber");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        when(patientRepository.findByPhoneNumber("+380991234567")).thenReturn(Optional.of(new Patient()));

        assertFalse(validator.isValid("+380991234567", context));
    }

    @Test
    public void testIsValid_UnknownField_ShouldReturnTrue() {
        when(annotation.field()).thenReturn("unknownField");
        when(annotation.role()).thenReturn("PATIENT");
        validator.initialize(annotation);

        assertTrue(validator.isValid("someValue", context));
    }

    @Test
    public void testIsValid_PassportNumber_Doctor_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("passportNumber");
        when(annotation.role()).thenReturn("DOCTOR");
        validator.initialize(annotation);

        when(doctorRepository.findByPassportNumber("CD789012")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("CD789012", context));
    }

    @Test
    public void testIsValid_PhoneNumber_Doctor_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("phoneNumber");
        when(annotation.role()).thenReturn("DOCTOR");
        validator.initialize(annotation);

        when(doctorRepository.findByPhoneNumber("+380997654321")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("+380997654321", context));
    }

    @Test
    public void testIsValid_PassportNumber_LabAssistant_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("passportNumber");
        when(annotation.role()).thenReturn("LAB_ASSISTANT");
        validator.initialize(annotation);

        when(labAssistantRepository.findByPassportNumber("EF345678")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("EF345678", context));
    }

    @Test
    public void testIsValid_PhoneNumber_LabAssistant_ShouldReturnTrueWhenNotExists() {
        when(annotation.field()).thenReturn("phoneNumber");
        when(annotation.role()).thenReturn("LAB_ASSISTANT");
        validator.initialize(annotation);

        when(labAssistantRepository.findByPhoneNumber("+380991112233")).thenReturn(Optional.empty());

        assertTrue(validator.isValid("+380991112233", context));
    }
}

