package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.DoctorRegisterRequest;
import kma.health.app.kma_health.dto.PatientRegisterRequest;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.repository.*;
import kma.health.app.starter.service.SecretKeyProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private LabAssistantRepository labAssistantRepository;

    @Mock
    private DoctorTypeRepository doctorTypeRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RandomProfileImageService randomProfileImageService;

    @Mock
    private SecretKeyProvider keyProvider;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void testRegisterPatient_WithDoctorRegisterRequest() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.PATIENT);
        request.setEmail("patient@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380991234567");
        request.setFullName("John Doe");
        request.setBirthDate(LocalDate.of(1990, 1, 1));
        request.setPassportNumber("AB123456");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/images/profile.png");

        String result = registrationService.register(request);

        assertEquals("Patient registered successfully", result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testRegisterDoctor_Success() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.DOCTOR);
        request.setEmail("doctor@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380991234567");
        request.setFullName("Dr. Smith");
        request.setBirthDate(LocalDate.of(1980, 5, 15));
        request.setPassportNumber("CD789012");
        request.setRegisterKey("valid-key");
        request.setType("Specialist");
        request.setDescription("Cardiologist with 10 years experience");
        request.setDoctorTypeId(1L);
        request.setHospitalId(1L);
        request.setStartedWorking(LocalDate.of(2010, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setId(1L);
        doctorType.setTypeName("Cardiologist");

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/images/profile.png");
        when(doctorTypeRepository.findById(1L)).thenReturn(Optional.of(doctorType));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));

        String result = registrationService.register(request);

        assertEquals("Doctor registered successfully", result);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void testRegisterLabAssistant_Success() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.LAB_ASSISTANT);
        request.setEmail("lab@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380991234567");
        request.setFullName("Lab Tech");
        request.setBirthDate(LocalDate.of(1985, 3, 20));
        request.setPassportNumber("EF345678");
        request.setRegisterKey("valid-key");
        request.setLabHospitalId(1L);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/images/profile.png");
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));

        String result = registrationService.register(request);

        assertEquals("Lab Assistant registered successfully", result);
        verify(labAssistantRepository).save(any(LabAssistant.class));
    }

    @Test
    void testRegisterDoctor_InvalidRegisterKey() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.DOCTOR);
        request.setEmail("doctor@example.com");
        request.setRegisterKey("invalid-key");

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });

        assertTrue(exception.getMessage().contains("Invalid register key"));
    }

    @Test
    void testRegisterDoctor_NullRegisterKey() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.DOCTOR);
        request.setEmail("doctor@example.com");
        request.setRegisterKey(null);

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");

        assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });
    }

    @Test
    void testRegisterDoctor_DoctorTypeNotFound() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.DOCTOR);
        request.setRegisterKey("valid-key");
        request.setDoctorTypeId(999L);

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/img.png");
        when(doctorTypeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });
    }

    @Test
    void testRegisterDoctor_HospitalNotFound() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.DOCTOR);
        request.setRegisterKey("valid-key");
        request.setDoctorTypeId(1L);
        request.setHospitalId(999L);

        DoctorType doctorType = new DoctorType();

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/img.png");
        when(doctorTypeRepository.findById(1L)).thenReturn(Optional.of(doctorType));
        when(hospitalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });
    }

    @Test
    void testRegisterLabAssistant_HospitalNotFound() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.LAB_ASSISTANT);
        request.setRegisterKey("valid-key");
        request.setLabHospitalId(999L);

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/img.png");
        when(hospitalRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });
    }

    @Test
    void testRegisterPatient_WithPatientRegisterRequest() {
        PatientRegisterRequest request = new PatientRegisterRequest();
        request.setEmail("patient@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380991234567");
        request.setFullName("Jane Doe");
        request.setBirthDate(LocalDate.of(1995, 6, 10));
        request.setPassportNumber("GH901234");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/images/profile.png");

        String result = registrationService.register(request);

        assertEquals("Patient registered successfully", result);
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void testRegisterDoctor_OtherRuntimeException() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.DOCTOR);
        request.setEmail("doctor@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380991234567");
        request.setFullName("Dr. Test");
        request.setBirthDate(LocalDate.of(1980, 1, 1));
        request.setPassportNumber("123456789");
        request.setRegisterKey("valid-key");
        request.setDoctorTypeId(1L);

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/img.png");
        when(doctorTypeRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            registrationService.register(request);
        });

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void testRegisterDoctor_TransactionSystemException() {
        DoctorRegisterRequest request = new DoctorRegisterRequest();
        request.setRole(UserRole.DOCTOR);
        request.setEmail("doctor@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+380991234567");
        request.setFullName("Dr. Test");
        request.setBirthDate(LocalDate.of(1980, 1, 1));
        request.setPassportNumber("123456789");
        request.setRegisterKey("valid-key");
        request.setDoctorTypeId(1L);
        request.setHospitalId(1L);
        request.setStartedWorking(LocalDate.of(2010, 1, 1));

        DoctorType doctorType = new DoctorType();
        Hospital hospital = new Hospital();

        when(keyProvider.getRegistrationKey()).thenReturn("valid-key");
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(randomProfileImageService.getRandomProfilePicture()).thenReturn("/img.png");
        when(doctorTypeRepository.findById(1L)).thenReturn(Optional.of(doctorType));
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));

        org.springframework.transaction.TransactionSystemException tse = 
            new org.springframework.transaction.TransactionSystemException("Transaction failed", 
                new jakarta.validation.ConstraintViolationException(java.util.Set.of()));
        
        when(doctorRepository.save(any(Doctor.class))).thenThrow(tse);

        assertThrows(org.springframework.transaction.TransactionSystemException.class, () -> {
            registrationService.register(request);
        });
    }
}

