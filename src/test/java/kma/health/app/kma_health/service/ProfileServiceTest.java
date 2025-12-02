package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.ProfileDto;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.repository.DoctorRepository;
import kma.health.app.kma_health.repository.LabAssistantRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private LabAssistantRepository labAssistantRepository;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private ProfileService profileService;

    @Test
    public void testGetProfileData_ForDoctor_ShouldReturnDoctorProfile() {
        UUID userId = UUID.randomUUID();
        
        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        
        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");
        
        Doctor doctor = new Doctor();
        doctor.setId(userId);
        doctor.setFullName("Dr. Smith");
        doctor.setEmail("dr.smith@example.com");
        doctor.setBirthDate(LocalDate.of(1980, 1, 1));
        doctor.setDoctorType(doctorType);
        doctor.setHospital(hospital);

        when(doctorRepository.findById(userId)).thenReturn(Optional.of(doctor));

        ProfileDto result = profileService.getProfileData(userId, "DOCTOR");

        assertNotNull(result);
        assertEquals("Dr. Smith", result.getFullName());
    }

    @Test
    public void testGetProfileData_ForDoctor_ShouldThrowExceptionWhenNotFound() {
        UUID userId = UUID.randomUUID();

        when(doctorRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            profileService.getProfileData(userId, "DOCTOR");
        });
    }

    @Test
    public void testGetProfileData_ForPatient_ShouldReturnPatientProfile() {
        UUID userId = UUID.randomUUID();
        
        Patient patient = new Patient();
        patient.setId(userId);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));

        Doctor familyDoctor = new Doctor();
        familyDoctor.setFullName("Dr. Family");

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(patientService.getFamilyDoctor(userId)).thenReturn(familyDoctor);
        when(patientService.getScheduledAppointments(userId)).thenReturn(Collections.emptyList());

        ProfileDto result = profileService.getProfileData(userId, "PATIENT");

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals("Dr. Family", result.getFamilyDoctorName());
    }

    @Test
    public void testGetProfileData_ForPatient_ShouldHandleNoFamilyDoctor() {
        UUID userId = UUID.randomUUID();
        
        Patient patient = new Patient();
        patient.setId(userId);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(patientService.getFamilyDoctor(userId)).thenThrow(new EntityNotFoundException("Declaration not found"));
        when(patientService.getScheduledAppointments(userId)).thenReturn(Collections.emptyList());

        ProfileDto result = profileService.getProfileData(userId, "PATIENT");

        assertNotNull(result);
        assertNull(result.getFamilyDoctorName());
    }

    @Test
    public void testGetProfileData_ForPatient_ShouldThrowExceptionWhenNotFound() {
        UUID userId = UUID.randomUUID();

        when(patientRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            profileService.getProfileData(userId, "PATIENT");
        });
    }

    @Test
    public void testGetProfileData_ForLabAssistant_ShouldReturnLabAssistantProfile() {
        UUID userId = UUID.randomUUID();
        
        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");
        
        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(userId);
        labAssistant.setFullName("Lab Assistant");
        labAssistant.setEmail("lab@example.com");
        labAssistant.setBirthDate(LocalDate.of(1985, 5, 15));
        labAssistant.setHospital(hospital);

        when(labAssistantRepository.findById(userId)).thenReturn(Optional.of(labAssistant));

        ProfileDto result = profileService.getProfileData(userId, "LAB_ASSISTANT");

        assertNotNull(result);
        assertEquals("Lab Assistant", result.getFullName());
    }

    @Test
    public void testGetProfileData_ForLabAssistant_ShouldThrowExceptionWhenNotFound() {
        UUID userId = UUID.randomUUID();

        when(labAssistantRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            profileService.getProfileData(userId, "LAB_ASSISTANT");
        });
    }

    @Test
    public void testGetProfileData_ForUnknownRole_ShouldThrowException() {
        UUID userId = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class, () -> {
            profileService.getProfileData(userId, "UNKNOWN_ROLE");
        });
    }

    @Test
    public void testGetProfileData_ForPatientWithScheduledAppointments_ShouldIncludeAppointments() {
        UUID userId = UUID.randomUUID();
        
        Patient patient = new Patient();
        patient.setId(userId);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Smith");
        
        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Family doctor");
        doctor.setDoctorType(doctorType);

        Referral referral = new Referral();
        referral.setPatient(patient);
        referral.setDoctorType(doctorType);

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setDoctor(doctor);
        appointment.setDate(LocalDate.now().plusDays(7));
        appointment.setReferral(referral);

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(patientService.getFamilyDoctor(userId)).thenThrow(new EntityNotFoundException("Not found"));
        when(patientService.getScheduledAppointments(userId)).thenReturn(Collections.singletonList(appointment));

        ProfileDto result = profileService.getProfileData(userId, "PATIENT");

        assertNotNull(result);
        assertNotNull(result.getPlannedAppointments());
        assertEquals(1, result.getPlannedAppointments().size());
    }

    @Test
    public void testGetProfileData_ForPatient_WithNullFamilyDoctor() {
        UUID userId = UUID.randomUUID();
        
        Patient patient = new Patient();
        patient.setId(userId);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(patientService.getFamilyDoctor(userId)).thenReturn(null);
        when(patientService.getScheduledAppointments(userId)).thenReturn(Collections.emptyList());

        ProfileDto result = profileService.getProfileData(userId, "PATIENT");

        assertNotNull(result);
        assertNull(result.getFamilyDoctorName());
    }
}

