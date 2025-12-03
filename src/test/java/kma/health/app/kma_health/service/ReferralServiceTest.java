package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.AppointmentStatus;
import kma.health.app.kma_health.exception.InvalidFamilyDoctorReferralMethodException;
import kma.health.app.kma_health.exception.MissingOpenAppointmentException;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.DoctorTypeRepository;
import kma.health.app.kma_health.repository.ReferralRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReferralServiceTest {

    @Mock
    private ReferralRepository referralRepository;

    @Mock
    private DoctorTypeRepository doctorTypeRepository;

    @Mock
    private ExaminationService examinationService;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private ReferralService referralService;

    @Test
    public void testCreateReferral_WithDoctorType_ShouldCreateReferral() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Appointment openAppointment = new Appointment();
        openAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(openAppointment));
        when(doctorTypeRepository.findByTypeName("Cardiologist")).thenReturn(Optional.of(doctorType));

        referralService.createReferralForDoctor(doctor, patient, "Cardiologist");

        verify(referralRepository, times(1)).save(any(Referral.class));
    }

    @Test
    public void testCreateReferral_WithoutOpenAppointment_ShouldThrowException() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.emptyList());

        assertThrows(MissingOpenAppointmentException.class,
                () -> referralService.createReferralForDoctor(doctor, patient, "Cardiologist"));

        verify(referralRepository, never()).save(any(Referral.class));
    }

    @Test
    public void testCreateReferral_ForFamilyDoctor_ShouldThrowException() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment openAppointment = new Appointment();
        openAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(openAppointment));

        assertThrows(InvalidFamilyDoctorReferralMethodException.class, () -> {
            referralService.createReferralForDoctor(doctor, patient, "Family doctor");
        });
    }

    @Test
    public void testCreateReferral_WithExamination_ShouldCreateReferral() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        Long examinationId = 1L;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        Examination examination = new Examination();
        examination.setId(examinationId);
        examination.setExamName("Blood Test");

        Appointment openAppointment = new Appointment();
        openAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(openAppointment));
        when(examinationService.findExaminationByName("Blood Test")).thenReturn(examination);

        referralService.createReferralForExamination(doctor, patient, "Blood Test");

        verify(referralRepository, times(1)).save(any(Referral.class));
    }

    @Test
    public void testCreateReferralForFamilyDoctor_ShouldCreateReferral() {
        UUID patientId = UUID.randomUUID();
        LocalDate appointmentDate = LocalDate.now().plusDays(7);

        Patient patient = new Patient();
        patient.setId(patientId);

        DoctorType familyDoctorType = new DoctorType();
        familyDoctorType.setTypeName("Family doctor");

        Referral savedReferral = new Referral();
        savedReferral.setId(UUID.randomUUID());

        when(doctorTypeRepository.findByTypeName("Family doctor")).thenReturn(Optional.of(familyDoctorType));
        when(referralRepository.save(any(Referral.class))).thenReturn(savedReferral);

        Referral result = referralService.createReferralForFamilyDoctor(patient, appointmentDate);

        assertNotNull(result);
        verify(referralRepository, times(1)).save(any(Referral.class));
    }

    @Test
    public void testDeleteReferral_ShouldDeleteExistingReferral() {
        UUID referralId = UUID.randomUUID();
        Referral referral = new Referral();
        referral.setId(referralId);

        when(referralRepository.existsById(referralId)).thenReturn(true);

        referralService.deleteReferral(referral);

        verify(referralRepository, times(1)).delete(referral);
    }

    @Test
    public void testDeleteReferral_ShouldNotDeleteIfNotExists() {
        UUID referralId = UUID.randomUUID();
        Referral referral = new Referral();
        referral.setId(referralId);

        when(referralRepository.existsById(referralId)).thenReturn(false);

        referralService.deleteReferral(referral);

        verify(referralRepository, never()).delete(any(Referral.class));
    }

    @Test
    public void testGetAllReferrals_ShouldReturnReferralsList() {
        UUID patientId = UUID.randomUUID();
        Referral referral1 = new Referral();
        Referral referral2 = new Referral();

        when(referralRepository.findByPatientId(patientId)).thenReturn(Arrays.asList(referral1, referral2));

        List<Referral> result = referralService.getAllReferrals(patientId);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetActiveReferrals_ShouldReturnOnlyActiveReferrals() {
        UUID patientId = UUID.randomUUID();

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Test");

        Patient patient = new Patient();
        patient.setId(patientId);

        Referral activeReferral = new Referral();
        activeReferral.setId(UUID.randomUUID());
        activeReferral.setValidUntil(LocalDate.now().plusDays(30));
        activeReferral.setDoctorType(doctorType);
        activeReferral.setPatient(patient);
        activeReferral.setDoctor(doctor);

        when(referralRepository.findByPatientIdAndValidUntilGreaterThanEqual(eq(patientId), any(LocalDate.class)))
                .thenReturn(Collections.singletonList(activeReferral));

        List<ReferralDto> result = referralService.getActiveReferrals(patientId);

        assertEquals(1, result.size());
    }

    @Test
    public void testGetActiveReferrals_ShouldReturnEmptyListWhenNoActiveReferrals() {
        UUID patientId = UUID.randomUUID();

        when(referralRepository.findByPatientIdAndValidUntilGreaterThanEqual(eq(patientId), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        List<ReferralDto> result = referralService.getActiveReferrals(patientId);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateReferral_WithExamination_ExaminationNotFound_ShouldThrowException() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();
        Long examinationId = 999L;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment openAppointment = new Appointment();
        openAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(openAppointment));
        when(examinationService.findExaminationByName("NonExistent"))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> {
            referralService.createReferralForExamination(doctor, patient, "NonExistent");
        });
    }

    @Test
    public void testCreateReferral_WithExamination_WithoutOpenAppointment_ShouldThrowException() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.emptyList());

        assertThrows(MissingOpenAppointmentException.class, () -> {
            referralService.createReferralForExamination(doctor, patient, "X-Ray");
        });
    }

    @Test
    public void testGetActiveReferrals_ShouldFilterOutUsedReferrals() {
        UUID patientId = UUID.randomUUID();

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Test");

        Patient patient = new Patient();
        patient.setId(patientId);

        Referral usedReferral = new Referral();
        usedReferral.setId(UUID.randomUUID());
        usedReferral.setValidUntil(LocalDate.now().plusDays(30));
        usedReferral.setDoctorType(doctorType);
        usedReferral.setPatient(patient);
        usedReferral.setDoctor(doctor);

        Referral unusedReferral = new Referral();
        unusedReferral.setId(UUID.randomUUID());
        unusedReferral.setValidUntil(LocalDate.now().plusDays(30));
        unusedReferral.setDoctorType(doctorType);
        unusedReferral.setPatient(patient);
        unusedReferral.setDoctor(doctor);

        when(referralRepository.findByPatientIdAndValidUntilGreaterThanEqual(eq(patientId), any(LocalDate.class)))
                .thenReturn(Arrays.asList(usedReferral, unusedReferral));

        when(appointmentRepository.existsByReferral_IdAndStatusNot(usedReferral.getId(), AppointmentStatus.MISSED))
                .thenReturn(true);
        when(appointmentRepository.existsByReferral_IdAndStatusNot(unusedReferral.getId(), AppointmentStatus.MISSED))
                .thenReturn(false);

        List<ReferralDto> result = referralService.getActiveReferrals(patientId);

        assertEquals(1, result.size());
    }

    @Test
    public void testCreateReferral_DoctorTypeNotFound_ShouldThrowException() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment openAppointment = new Appointment();
        openAppointment.setStatus(AppointmentStatus.OPEN);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(openAppointment));
        when(doctorTypeRepository.findByTypeName("NonExistent"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            referralService.createReferralForDoctor(doctor, patient, "NonExistent");
        });
    }

    @Test
    public void testCreateReferralForFamilyDoctor_DoctorTypeNotFound_ShouldThrowException() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        when(doctorTypeRepository.findByTypeName("Family doctor"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            referralService.createReferralForFamilyDoctor(patient, LocalDate.now());
        });
    }

    @Test
    public void testHaveOpenAppointment_WithScheduledStatus() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment scheduledAppointment = new Appointment();
        scheduledAppointment.setStatus(AppointmentStatus.SCHEDULED);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(scheduledAppointment));

        assertThrows(MissingOpenAppointmentException.class, () -> {
            referralService.createReferralForDoctor(doctor, patient, "Cardiologist");
        });
    }

    @Test
    public void testHaveOpenAppointment_WithNullStatus() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);

        Patient patient = new Patient();
        patient.setId(patientId);

        Appointment appointmentWithNullStatus = new Appointment();
        appointmentWithNullStatus.setStatus(null);

        when(appointmentRepository.findByDoctor_IdAndReferral_Patient_Id(doctorId, patientId))
                .thenReturn(Collections.singletonList(appointmentWithNullStatus));

        assertThrows(MissingOpenAppointmentException.class, () -> {
            referralService.createReferralForDoctor(doctor, patient, "Cardiologist");
        });
    }
}
