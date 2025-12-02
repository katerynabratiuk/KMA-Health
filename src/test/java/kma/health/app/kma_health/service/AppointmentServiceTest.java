package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.AppointmentCreateUpdateDto;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.exception.AppointmentTargetConflictException;
import kma.health.app.kma_health.exception.DoctorSpecializationAgeRestrictionException;
import kma.health.app.kma_health.repository.*;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private ReferralRepository referralRepository;

    @Mock
    private MedicalFileRepository medicalFileRepository;

    @Mock
    private LabAssistantRepository labAssistantRepository;

    @Mock
    private DoctorTypeRepository doctorTypeRepository;

    @Mock
    private HospitalService hospitalService;

    @Mock
    private ReferralService referralService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    public void testCreateAppointment_ShouldThrowExceptionWhenBothDoctorAndHospitalProvided() {
        UUID userId = UUID.randomUUID();

        AppointmentCreateUpdateDto appointmentDto = new AppointmentCreateUpdateDto();
        appointmentDto.setDoctorId(UUID.randomUUID());
        appointmentDto.setHospitalId(1L);
        appointmentDto.setPatientId(userId);

        assertThrows(AppointmentTargetConflictException.class, () -> {
            appointmentService.createAppointment(appointmentDto, userId);
        });
    }

    @Test
    public void testCreateAppointment_ShouldThrowExceptionWhenNeitherDoctorNorHospitalProvided() {
        UUID userId = UUID.randomUUID();

        AppointmentCreateUpdateDto appointmentDto = new AppointmentCreateUpdateDto();
        appointmentDto.setPatientId(userId);

        assertThrows(AppointmentTargetConflictException.class, () -> {
            appointmentService.createAppointment(appointmentDto, userId);
        });
    }

    @Test
    public void testGetAppointmentsForPatient_ShouldReturnAppointmentsList() {
        UUID patientId = UUID.randomUUID();
        
        Patient patient = new Patient();
        patient.setId(patientId);
        
        Referral referral = new Referral();
        referral.setPatient(patient);
        
        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Family doctor");
        referral.setDoctorType(doctorType);
        
        Doctor doctor = new Doctor();
        doctor.setFullName("Dr. Test");
        doctor.setDoctorType(doctorType);
        
        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setDate(LocalDate.now());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findByReferral_Patient_Id(patientId))
                .thenReturn(Collections.singletonList(appointment));

        List<AppointmentFullViewDto> result = appointmentService.getAppointmentsForPatient(patientId);

        assertEquals(1, result.size());
    }

    @Test
    public void testGetAppointmentsForPatient_WithDateRange_ShouldReturnAppointmentsList() {
        UUID patientId = UUID.randomUUID();
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);

        when(appointmentRepository.findByReferral_Patient_idAndDateBetween(patientId, start, end))
                .thenReturn(Collections.emptyList());

        List<AppointmentShortViewDto> result = appointmentService.getAppointmentsForPatient(patientId, start, end);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAppointmentsForDoctor_ShouldReturnAppointmentsList() {
        UUID doctorId = UUID.randomUUID();
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(7);

        when(appointmentRepository.findByDoctor_IdAndDateBetween(doctorId, start, end))
                .thenReturn(Collections.emptyList());

        List<AppointmentShortViewDto> result = appointmentService.getAppointmentsForDoctor(doctorId, start, end);

        assertNotNull(result);
    }

    @Test
    public void testDeleteAppointment_ShouldDeleteExistingAppointment() {
        UUID appointmentId = UUID.randomUUID();

        when(appointmentRepository.existsById(appointmentId)).thenReturn(true);

        appointmentService.deleteAppointment(appointmentId);

        verify(appointmentRepository, times(1)).deleteById(appointmentId);
    }

    @Test
    public void testDeleteAppointment_ShouldThrowExceptionWhenNotFound() {
        UUID appointmentId = UUID.randomUUID();

        when(appointmentRepository.existsById(appointmentId)).thenReturn(false);

        assertThrows(AppointmentNotFoundException.class, () -> {
            appointmentService.deleteAppointment(appointmentId);
        });
    }

    @Test
    public void testValidateDoctorAndPatientAge_ShouldThrowExceptionForUnderagePatientWithAdultDoctor() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient underagePatient = new Patient();
        underagePatient.setId(patientId);
        underagePatient.setBirthDate(LocalDate.now().minusYears(10));

        Doctor adultDoctor = new Doctor();
        adultDoctor.setId(doctorId);
        adultDoctor.setType("adult");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(underagePatient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(adultDoctor));

        assertThrows(DoctorSpecializationAgeRestrictionException.class, () -> {
            appointmentService.validateDoctorAndPatientAge(doctorId, patientId);
        });
    }

    @Test
    public void testValidateDoctorAndPatientAge_ShouldThrowExceptionForAdultPatientWithChildDoctor() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient adultPatient = new Patient();
        adultPatient.setId(patientId);
        adultPatient.setBirthDate(LocalDate.now().minusYears(25));

        Doctor childDoctor = new Doctor();
        childDoctor.setId(doctorId);
        childDoctor.setType("child");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(adultPatient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(childDoctor));

        assertThrows(DoctorSpecializationAgeRestrictionException.class, () -> {
            appointmentService.validateDoctorAndPatientAge(doctorId, patientId);
        });
    }

    @Test
    public void testValidateDoctorAndPatientAge_ShouldPassForChildPatientWithChildDoctor() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient childPatient = new Patient();
        childPatient.setId(patientId);
        childPatient.setBirthDate(LocalDate.now().minusYears(10));

        Doctor childDoctor = new Doctor();
        childDoctor.setId(doctorId);
        childDoctor.setType("child");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(childPatient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(childDoctor));

        assertDoesNotThrow(() -> {
            appointmentService.validateDoctorAndPatientAge(doctorId, patientId);
        });
    }

    @Test
    public void testValidateDoctorAndPatientAge_ShouldPassForAdultPatientWithAdultDoctor() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient adultPatient = new Patient();
        adultPatient.setId(patientId);
        adultPatient.setBirthDate(LocalDate.now().minusYears(25));

        Doctor adultDoctor = new Doctor();
        adultDoctor.setId(doctorId);
        adultDoctor.setType("adult");

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(adultPatient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(adultDoctor));

        assertDoesNotThrow(() -> {
            appointmentService.validateDoctorAndPatientAge(doctorId, patientId);
        });
    }

    @Test
    public void testValidateDoctorAndPatientAge_ShouldDoNothingWhenDoctorIdIsNull() {
        UUID patientId = UUID.randomUUID();

        assertDoesNotThrow(() -> {
            appointmentService.validateDoctorAndPatientAge(null, patientId);
        });

        verify(patientRepository, never()).findById(any());
        verify(doctorRepository, never()).findById(any());
    }

    @Test
    public void testHaveOpenAppointment_ShouldReturnTrueWhenOpenAppointmentExists() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment openAppointment = new Appointment();
        openAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(openAppointment));

        assertTrue(appointmentService.haveOpenAppointment(doctorId, patientId));
    }

    @Test
    public void testHaveOpenAppointment_ShouldReturnFalseWhenNoOpenAppointment() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment scheduledAppointment = new Appointment();
        scheduledAppointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(scheduledAppointment));

        assertFalse(appointmentService.haveOpenAppointment(doctorId, patientId));
    }

    @Test
    public void testHaveOpenAppointment_ShouldReturnFalseWhenNoAppointments() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.emptyList());

        assertFalse(appointmentService.haveOpenAppointment(doctorId, patientId));
    }

    @Test
    public void testOpenAppointments_ShouldChangeStatusToOpen() {
        Appointment scheduledAppointment = new Appointment();
        scheduledAppointment.setId(UUID.randomUUID());
        scheduledAppointment.setStatus(AppointmentStatus.SCHEDULED);
        scheduledAppointment.setDate(LocalDate.now().minusDays(1));
        scheduledAppointment.setTime(LocalTime.of(10, 0));

        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(Collections.singletonList(scheduledAppointment));

        appointmentService.openAppointments();

        assertEquals(AppointmentStatus.OPEN, scheduledAppointment.getStatus());
        verify(appointmentRepository, times(1)).saveAll(any());
    }

    @Test
    public void testGetAppointmentsForPatient_WithSingleDate() {
        UUID patientId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        when(appointmentRepository.findByReferral_Patient_idAndDateBetween(patientId, date, date))
                .thenReturn(Collections.emptyList());

        List<AppointmentShortViewDto> result = appointmentService.getAppointmentsForPatient(patientId, date);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetAppointmentsForDoctor_WithSingleDate() {
        UUID doctorId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        when(appointmentRepository.findByDoctor_IdAndDateBetween(doctorId, date, date))
                .thenReturn(Collections.emptyList());

        List<AppointmentShortViewDto> result = appointmentService.getAppointmentsForDoctor(doctorId, date);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testOpenAppointments_ShouldNotChangeStatusForFutureAppointments() {
        Appointment futureAppointment = new Appointment();
        futureAppointment.setId(UUID.randomUUID());
        futureAppointment.setStatus(AppointmentStatus.SCHEDULED);
        futureAppointment.setDate(LocalDate.now().plusDays(1));
        futureAppointment.setTime(LocalTime.of(10, 0));

        when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED))
                .thenReturn(Collections.singletonList(futureAppointment));

        appointmentService.openAppointments();

        assertEquals(AppointmentStatus.SCHEDULED, futureAppointment.getStatus());
    }

    @Test
    public void testHaveOpenAppointment_ShouldReturnFalseWhenStatusIsNull() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setStatus(null);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(appointment));

        assertFalse(appointmentService.haveOpenAppointment(doctorId, patientId));
    }

    @Test
    public void testCreateAppointment_ShouldThrowExceptionWhenPatientIdDoesNotMatch() {
        UUID userId = UUID.randomUUID();
        UUID differentPatientId = UUID.randomUUID();

        AppointmentCreateUpdateDto dto = new AppointmentCreateUpdateDto();
        dto.setPatientId(differentPatientId);
        dto.setDoctorId(UUID.randomUUID());

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.createAppointment(dto, userId);
        });
    }

    @Test
    public void testGetFullAppointment_AsPatient() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);

        Referral referral = new Referral();
        referral.setPatient(patient);

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Family doctor");
        referral.setDoctorType(doctorType);

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Test");
        doctor.setDoctorType(doctorType);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setDate(LocalDate.now());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentFullViewDto result = appointmentService.getFullAppointment(appointmentId, patientId);

        assertNotNull(result);
    }

    @Test
    public void testGetFullAppointment_AsDoctor_ScheduledAppointment() {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.getFullAppointment(appointmentId, doctorId);
        });
    }

    @Test
    public void testGetFullAppointment_Unauthorized() {
        UUID appointmentId = UUID.randomUUID();
        UUID randomUserId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.getFullAppointment(appointmentId, randomUserId);
        });
    }

    @Test
    public void testAssignLabAssistantToAppointment_Success() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Hospital hospital = new Hospital();
        hospital.setId(1L);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);
        labAssistant.setHospital(hospital);

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setHospital(hospital);
        appointment.setReferral(referral);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(labAssistantRepository.findById(labAssistantId)).thenReturn(Optional.of(labAssistant));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        appointmentService.assignLabAssistantToAppointment(appointmentId, labAssistantId);

        assertEquals(labAssistant, appointment.getLabAssistant());
    }

    @Test
    public void testAssignLabAssistantToAppointment_AlreadyFinished() {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.FINISHED);

        LabAssistant labAssistant = new LabAssistant();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(labAssistantRepository.findById(labAssistantId)).thenReturn(Optional.of(labAssistant));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.assignLabAssistantToAppointment(appointmentId, labAssistantId);
        });
    }

    @Test
    public void testAssignLabAssistantToAppointment_AlreadyAssigned() {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.OPEN);
        appointment.setLabAssistant(new LabAssistant());

        LabAssistant labAssistant = new LabAssistant();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(labAssistantRepository.findById(labAssistantId)).thenReturn(Optional.of(labAssistant));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.assignLabAssistantToAppointment(appointmentId, labAssistantId);
        });
    }

    @Test
    public void testAssignLabAssistantToAppointment_DoctorTarget() {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Appointment appointment = new Appointment();
        appointment.setStatus(AppointmentStatus.OPEN);
        appointment.setDoctor(new Doctor());

        LabAssistant labAssistant = new LabAssistant();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(labAssistantRepository.findById(labAssistantId)).thenReturn(Optional.of(labAssistant));

        assertThrows(AppointmentTargetConflictException.class, () -> {
            appointmentService.assignLabAssistantToAppointment(appointmentId, labAssistantId);
        });
    }

    @Test
    public void testCancelAppointment_AsPatient_Success() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);

        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(null, patientId, appointmentId);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    public void testCancelAppointment_AsDoctor_Success() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(doctorId, null, appointmentId);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    public void testCancelAppointment_AsDoctor_NotOpen() {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.cancelAppointment(doctorId, null, appointmentId);
        });
    }

    @Test
    public void testCancelAppointment_AsPatient_AlreadyOpen() {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);

        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.cancelAppointment(null, patientId, appointmentId);
        });
    }

    @Test
    public void testFinishAppointment_AsDoctor_Success() throws IOException {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        appointmentService.finishAppointment(doctorId, appointmentId, "Test diagnosis", null);

        assertEquals(AppointmentStatus.FINISHED, appointment.getStatus());
        assertEquals("Test diagnosis", appointment.getDiagnosis());
    }

    @Test
    public void testFinishAppointment_WrongDoctor() {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID wrongDoctorId = UUID.randomUUID();

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.finishAppointment(wrongDoctorId, appointmentId, "Test", null);
        });
    }

    @Test
    public void testFinishAppointment_NotOpen() {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.finishAppointment(doctorId, appointmentId, "Test", null);
        });
    }

    @Test
    public void testGetFullAppointment_AsLabAssistant_Open() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Radiologist");
        referral.setDoctorType(doctorType);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setLabAssistant(labAssistant);
        appointment.setDate(LocalDate.now());
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentFullViewDto result = appointmentService.getFullAppointment(appointmentId, labAssistantId);

        assertNotNull(result);
    }

    @Test
    public void testCancelAppointment_AsPatient_WrongPatient() {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        UUID wrongPatientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);

        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.cancelAppointment(null, wrongPatientId, appointmentId);
        });
    }

    @Test
    public void testCancelAppointment_AsDoctor_WrongDoctor() {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();
        UUID wrongDoctorId = UUID.randomUUID();

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.cancelAppointment(wrongDoctorId, null, appointmentId);
        });
    }

    @Test
    public void testAssignLabAssistant_WrongHospital() {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);
        labAssistant.setHospital(hospital2);

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setHospital(hospital1);
        appointment.setReferral(referral);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(labAssistantRepository.findById(labAssistantId)).thenReturn(Optional.of(labAssistant));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.assignLabAssistantToAppointment(appointmentId, labAssistantId);
        });
    }

    @Test
    public void testFinishAppointment_AsLabAssistant_Success() throws IOException {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setLabAssistant(labAssistant);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        appointmentService.finishAppointment(labAssistantId, appointmentId, "Test result", null);

        assertEquals(AppointmentStatus.FINISHED, appointment.getStatus());
    }

    @Test
    public void testGetPublicAppointmentsForDoctor() {
        UUID doctorId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(UUID.randomUUID());
        appointment.setDoctor(doctor);
        appointment.setReferral(referral);
        appointment.setDate(date);
        appointment.setTime(LocalTime.of(10, 0));

        when(appointmentRepository.findByDoctor_IdAndDateBetween(doctorId, date, date))
                .thenReturn(Collections.singletonList(appointment));

        var result = appointmentService.getPublicAppointmentsForDoctor(doctorId, date);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetFullAppointment_WithNullDoctor() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Family doctor");

        Referral referral = new Referral();
        referral.setPatient(patient);
        referral.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(null);
        appointment.setHospital(hospital);
        appointment.setDate(LocalDate.now());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentFullViewDto result = appointmentService.getFullAppointment(appointmentId, patientId);

        assertNotNull(result);
        assertNull(result.getDoctorId());
    }

    @Test
    public void testGetFullAppointment_AsLabAssistant_Scheduled_ShouldThrow() {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setLabAssistant(labAssistant);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.getFullAppointment(appointmentId, labAssistantId);
        });
    }

    @Test
    public void testCancelAppointment_AsLabAssistant_Success() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        Hospital hospital = new Hospital();
        hospital.setId(1L);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setHospital(hospital);
        appointment.setLabAssistant(labAssistant);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(labAssistantId, null, appointmentId);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    public void testCancelAppointment_AsLabAssistant_NotOpen_ShouldThrow() {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setLabAssistant(labAssistant);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.cancelAppointment(labAssistantId, null, appointmentId);
        });
    }

    @Test
    public void testCancelAppointment_AsLabAssistant_WrongLabAssistant_ShouldThrow() {
        UUID appointmentId = UUID.randomUUID();
        UUID labAssistantId = UUID.randomUUID();
        UUID wrongLabAssistantId = UUID.randomUUID();

        Patient patient = new Patient();
        Referral referral = new Referral();
        referral.setPatient(patient);

        LabAssistant labAssistant = new LabAssistant();
        labAssistant.setId(labAssistantId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setLabAssistant(labAssistant);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AccessDeniedException.class, () -> {
            appointmentService.cancelAppointment(wrongLabAssistantId, null, appointmentId);
        });
    }

    @Test
    public void testCancelAppointment_NoDoctorOrLabAssistant_AsPatient() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);

        Referral referral = new Referral();
        referral.setPatient(patient);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(null);
        appointment.setLabAssistant(null);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentService.cancelAppointment(null, patientId, appointmentId);

        verify(appointmentRepository).delete(appointment);
    }

    @Test
    public void testFinishAppointment_WithEmptyMedicalFiles() throws IOException {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        Referral referral = new Referral();
        referral.setPatient(patient);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        // Pass empty list instead of null
        appointmentService.finishAppointment(doctorId, appointmentId, "Test diagnosis", Collections.emptyList());

        assertEquals(AppointmentStatus.FINISHED, appointment.getStatus());
        assertEquals("Test diagnosis", appointment.getDiagnosis());
    }

    @Test
    public void testGetFullAppointment_AsDoctor_Open() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID doctorId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Referral referral = new Referral();
        referral.setPatient(patient);
        referral.setDoctorType(doctorType);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFullName("Dr. Test");
        doctor.setDoctorType(doctorType);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(doctor);
        appointment.setDate(LocalDate.now());
        appointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentFullViewDto result = appointmentService.getFullAppointment(appointmentId, doctorId);

        assertNotNull(result);
        assertEquals(doctorId, result.getDoctorId());
    }

    @Test
    public void testGetFullAppointment_WithNullLabAssistant() throws AccessDeniedException {
        UUID appointmentId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Patient patient = new Patient();
        patient.setId(patientId);

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Family doctor");

        Referral referral = new Referral();
        referral.setPatient(patient);
        referral.setDoctorType(doctorType);

        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setReferral(referral);
        appointment.setDoctor(null);
        appointment.setLabAssistant(null);
        appointment.setDate(LocalDate.now());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        AppointmentFullViewDto result = appointmentService.getFullAppointment(appointmentId, patientId);

        assertNotNull(result);
    }

    @Test
    public void testHaveOpenAppointment_WithFinishedStatus() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment finishedAppointment = new Appointment();
        finishedAppointment.setStatus(AppointmentStatus.FINISHED);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(finishedAppointment));

        assertFalse(appointmentService.haveOpenAppointment(doctorId, patientId));
    }

    @Test
    public void testHaveOpenAppointment_WithMultipleAppointments() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Appointment scheduledAppointment = new Appointment();
        scheduledAppointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment finishedAppointment = new Appointment();
        finishedAppointment.setStatus(AppointmentStatus.FINISHED);

        Appointment openAppointment = new Appointment();
        openAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Arrays.asList(scheduledAppointment, finishedAppointment, openAppointment));

        assertTrue(appointmentService.haveOpenAppointment(doctorId, patientId));
    }
}
