package kma.health.app.kma_health.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdultValidatorTest {

    private AdultValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private Adult adultAnnotation;

    @BeforeEach
    void setUp() {
        validator = new AdultValidator();
    }

    @Test
    public void testIsValid_ShouldReturnTrueForNullBirthDate() {
        when(adultAnnotation.minAge()).thenReturn(18);
        validator.initialize(adultAnnotation);

        assertTrue(validator.isValid(null, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueForAdult() {
        when(adultAnnotation.minAge()).thenReturn(18);
        validator.initialize(adultAnnotation);

        LocalDate adultBirthDate = LocalDate.now().minusYears(25);

        assertTrue(validator.isValid(adultBirthDate, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueForExactlyMinAge() {
        when(adultAnnotation.minAge()).thenReturn(18);
        validator.initialize(adultAnnotation);

        LocalDate exactlyAdultBirthDate = LocalDate.now().minusYears(18);

        assertTrue(validator.isValid(exactlyAdultBirthDate, context));
    }

    @Test
    public void testIsValid_ShouldReturnFalseForMinor() {
        when(adultAnnotation.minAge()).thenReturn(18);
        validator.initialize(adultAnnotation);

        LocalDate minorBirthDate = LocalDate.now().minusYears(15);

        assertFalse(validator.isValid(minorBirthDate, context));
    }

    @Test
    public void testIsValid_ShouldReturnFalseForJustUnderMinAge() {
        when(adultAnnotation.minAge()).thenReturn(18);
        validator.initialize(adultAnnotation);

        LocalDate justUnderAdultBirthDate = LocalDate.now().minusYears(18).plusDays(1);

        assertFalse(validator.isValid(justUnderAdultBirthDate, context));
    }

    @Test
    public void testIsValid_WithCustomMinAge() {
        when(adultAnnotation.minAge()).thenReturn(21);
        validator.initialize(adultAnnotation);

        LocalDate under21BirthDate = LocalDate.now().minusYears(20);
        LocalDate over21BirthDate = LocalDate.now().minusYears(25);

        assertFalse(validator.isValid(under21BirthDate, context));
        assertTrue(validator.isValid(over21BirthDate, context));
    }

    @Test
    public void testIsValid_ShouldReturnTrueForVeryOldPerson() {
        when(adultAnnotation.minAge()).thenReturn(18);
        validator.initialize(adultAnnotation);

        LocalDate veryOldBirthDate = LocalDate.now().minusYears(100);

        assertTrue(validator.isValid(veryOldBirthDate, context));
    }

    @Test
    public void testIsValid_ShouldReturnFalseForFutureBirthDate() {
        when(adultAnnotation.minAge()).thenReturn(18);
        validator.initialize(adultAnnotation);

        LocalDate futureBirthDate = LocalDate.now().plusYears(1);

        assertFalse(validator.isValid(futureBirthDate, context));
    }
}

