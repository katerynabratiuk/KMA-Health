package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.PatientContactsDto;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.PatientHistoryAccessException;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.DeclarationRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DeclarationRepository declarationRepository;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private ReferralService referralService;

    @InjectMocks
    private PatientService patientService;

    @Test
    public void testGetPatientById_ShouldReturnPatient() {
        UUID patientId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(patientId);

        when(patientRepository.getReferenceById(patientId)).thenReturn(patient);

        Patient result = patientService.getPatientById(patientId);

        assertEquals(patientId, result.getId());
        verify(patientRepository, times(1)).getReferenceById(patientId);
    }

    @Test
    public void testGetAppointments_ShouldReturnAppointmentsList() {
        UUID patientId = UUID.randomUUID();
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();

        when(appointmentRepository.findByReferral_Patient_Id(patientId))
                .thenReturn(Arrays.asList(appointment1, appointment2));

        List<Appointment> result = patientService.getAppointments(patientId);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetDeclaration_ShouldReturnDeclaration() {
        UUID patientId = UUID.randomUUID();
        Declaration declaration = new Declaration();

        when(declarationRepository.findById(patientId)).thenReturn(Optional.of(declaration));

        Declaration result = patientService.getDeclaration(patientId);

        assertNotNull(result);
    }

    @Test
    public void testGetDeclaration_ShouldThrowExceptionWhenNotFound() {
        UUID patientId = UUID.randomUUID();

        when(declarationRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            patientService.getDeclaration(patientId);
        });
    }

    @Test
    public void testGetFamilyDoctor_ShouldReturnDoctor() {
        UUID patientId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Smith");

        Declaration declaration = new Declaration();
        declaration.setDoctor(doctor);

        when(declarationRepository.findById(patientId)).thenReturn(Optional.of(declaration));

        Doctor result = patientService.getFamilyDoctor(patientId);

        assertEquals("Dr. Smith", result.getFullName());
    }

    @Test
    public void testGetScheduledAppointments_ShouldReturnScheduledOnly() {
        UUID patientId = UUID.randomUUID();
        Appointment scheduledAppointment = new Appointment();
        scheduledAppointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findByReferralPatientIdAndStatus(patientId, AppointmentStatus.SCHEDULED))
                .thenReturn(Collections.singletonList(scheduledAppointment));

        List<Appointment> result = patientService.getScheduledAppointments(patientId);

        assertEquals(1, result.size());
        assertEquals(AppointmentStatus.SCHEDULED, result.get(0).getStatus());
    }

    @Test
    public void testGetPatientContacts_ShouldReturnContactsDto() {
        UUID patientId = UUID.randomUUID();
        
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setPhoneNumber("+380991234567");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));

        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Smith");

        Declaration declaration = new Declaration();
        declaration.setDoctor(doctor);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(declarationRepository.findByPatientId(patientId)).thenReturn(Optional.of(declaration));

        PatientContactsDto result = patientService.getPatientContacts(patientId);

        assertNotNull(result);
    }

    @Test
    public void testGetPatientContacts_ShouldThrowExceptionWhenPatientNotFound() {
        UUID patientId = UUID.randomUUID();

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            patientService.getPatientContacts(patientId);
        });
    }

    @Test
    public void testGetPatientContacts_ShouldReturnDtoWhenNoDeclaration() {
        UUID patientId = UUID.randomUUID();
        
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setFullName("John Doe");
        patient.setEmail("john@example.com");
        patient.setPhoneNumber("+380991234567");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(declarationRepository.findByPatientId(patientId)).thenReturn(Optional.empty());

        PatientContactsDto result = patientService.getPatientContacts(patientId);

        assertNotNull(result);
    }

    @Test
    public void testGetPatientMedicalHistory_AsPatient_ShouldReturnHistory() {
        UUID patientId = UUID.randomUUID();
        List<AppointmentFullViewDto> expectedHistory = Collections.emptyList();

        when(appointmentService.getAppointmentsForPatient(patientId)).thenReturn(expectedHistory);

        List<AppointmentFullViewDto> result = patientService.getPatientMedicalHistory(patientId, null, UserRole.PATIENT);

        assertEquals(expectedHistory, result);
    }

    @Test
    public void testGetPatientMedicalHistory_AsDoctorWithOpenAppointment_ShouldReturnHistory() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        List<AppointmentFullViewDto> expectedHistory = Collections.emptyList();

        when(appointmentService.haveOpenAppointment(doctorId, patientId)).thenReturn(true);
        when(appointmentService.getAppointmentsForPatient(patientId)).thenReturn(expectedHistory);

        List<AppointmentFullViewDto> result = patientService.getPatientMedicalHistory(patientId, doctorId, UserRole.DOCTOR);

        assertEquals(expectedHistory, result);
    }

    @Test
    public void testGetPatientMedicalHistory_AsDoctorWithoutOpenAppointment_ShouldThrowException() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        when(appointmentService.haveOpenAppointment(doctorId, patientId)).thenReturn(false);

        assertThrows(PatientHistoryAccessException.class, () -> {
            patientService.getPatientMedicalHistory(patientId, doctorId, UserRole.DOCTOR);
        });
    }

    @Test
    public void testGetPatientMedicalHistory_AsOtherRole_ShouldThrowException() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        assertThrows(PatientHistoryAccessException.class, () -> {
            patientService.getPatientMedicalHistory(patientId, doctorId, UserRole.LAB_ASSISTANT);
        });
    }

    @Test
    public void testGetPatientReferrals_AsPatient_ShouldReturnReferrals() {
        UUID patientId = UUID.randomUUID();
        
        when(referralService.getAllReferrals(patientId)).thenReturn(Collections.emptyList());

        List<ReferralDto> result = patientService.getPatientReferrals(patientId, null, UserRole.PATIENT);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetPatientReferrals_AsDoctorWithOpenAppointment_ShouldReturnReferrals() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        when(appointmentService.haveOpenAppointment(doctorId, patientId)).thenReturn(true);
        when(referralService.getAllReferrals(patientId)).thenReturn(Collections.emptyList());

        List<ReferralDto> result = patientService.getPatientReferrals(patientId, doctorId, UserRole.DOCTOR);

        assertNotNull(result);
    }

    @Test
    public void testGetPatientReferrals_AsDoctorWithoutOpenAppointment_ShouldThrowException() {
        UUID patientId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        when(appointmentService.haveOpenAppointment(doctorId, patientId)).thenReturn(false);

        assertThrows(PatientHistoryAccessException.class, () -> {
            patientService.getPatientReferrals(patientId, doctorId, UserRole.DOCTOR);
        });
    }

    @Test
    public void testGetPatientReferrals_AsOtherRole_ShouldThrowException() {
        UUID patientId = UUID.randomUUID();

        assertThrows(PatientHistoryAccessException.class, () -> {
            patientService.getPatientReferrals(patientId, null, UserRole.LAB_ASSISTANT);
        });
    }
}

