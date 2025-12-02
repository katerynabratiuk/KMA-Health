package kma.health.app.kma_health.validator;

import jakarta.validation.ConstraintValidatorContext;
import kma.health.app.kma_health.dto.DoctorRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BirthBeforeWorkValidatorTest {

    private BirthBeforeWorkValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @BeforeEach
    void setUp() {
        validator = new BirthBeforeWorkValidator();
    }

    @Test
    public void testIsValid_ShouldReturnTrueForNullRequest() {
        assertTrue(validator.isValid(null, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueWhenBirthDateIsNull() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setBirthDate(null);
        request.setStartedWorking(LocalDate.now());

        assertTrue(validator.isValid(request, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueWhenStartedWorkingIsNull() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setStartedWorking(null);

        assertTrue(validator.isValid(request, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueWhenBothDatesAreNull() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setBirthDate(null);
        request.setStartedWorking(null);

        assertTrue(validator.isValid(request, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueWhenBirthDateIsBeforeStartedWorking() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setStartedWorking(LocalDate.of(2015, 1, 1));

        assertTrue(validator.isValid(request, context));
    }

    @Test
    public void testIsValid_ShouldReturnFalseWhenBirthDateIsAfterStartedWorking() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setBirthDate(LocalDate.of(2020, 1, 1));
        request.setStartedWorking(LocalDate.of(2015, 1, 1));

        when(context.getDefaultConstraintMessageTemplate()).thenReturn("Invalid date");
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        assertFalse(validator.isValid(request, context));

        verify(context).disableDefaultConstraintViolation();
        verify(violationBuilder).addPropertyNode("startedWorking");
    }

    @Test
    public void testIsValid_ShouldReturnFalseWhenDatesAreEqual() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        LocalDate sameDate = LocalDate.of(2000, 1, 1);
        request.setBirthDate(sameDate);
        request.setStartedWorking(sameDate);

        when(context.getDefaultConstraintMessageTemplate()).thenReturn("Invalid date");
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);

        assertFalse(validator.isValid(request, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueWhenBirthDateIsOneDayBeforeStartedWorking() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setBirthDate(LocalDate.of(2000, 1, 1));
        request.setStartedWorking(LocalDate.of(2000, 1, 2));

        assertTrue(validator.isValid(request, context));
    }
}

