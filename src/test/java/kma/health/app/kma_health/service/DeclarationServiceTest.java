package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.entity.Declaration;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.exception.DoctorTypeMismatchException;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeclarationServiceTest {

    @Mock
    private DeclarationRepository declarationRepository;

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private DeclarationService declarationService;

    @Test
    public void testCreateDeclaration_ShouldCreateDeclarationForFamilyDoctor() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        DoctorType familyType = new DoctorType();
        familyType.setTypeName("Family");

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setDoctorType(familyType);

        Patient patient = new Patient();
        patient.setId(patientId);

        doNothing().when(appointmentService).validateDoctorAndPatientAge(doctorId, patientId);

        declarationService.createDeclaration(doctor, patient);

        verify(declarationRepository, times(1)).save(any(Declaration.class));
    }

    @Test
    public void testCreateDeclaration_ShouldThrowExceptionForNonFamilyDoctor() {
        DoctorType specialistType = new DoctorType();
        specialistType.setTypeName("Cardiologist");

        Doctor doctor = new Doctor();
        doctor.setDoctorType(specialistType);

        Patient patient = new Patient();

        assertThrows(DoctorTypeMismatchException.class, () -> {
            declarationService.createDeclaration(doctor, patient);
        });

        verify(declarationRepository, never()).save(any(Declaration.class));
    }

    @Test
    public void testDeleteDeclaration_ShouldDeleteExistingDeclaration() {
        UUID declarationId = UUID.randomUUID();

        when(declarationRepository.existsById(declarationId)).thenReturn(true);

        declarationService.deleteDeclaration(declarationId);

        verify(declarationRepository, times(1)).deleteById(declarationId);
    }

    @Test
    public void testDeleteDeclaration_ShouldThrowExceptionWhenNotFound() {
        UUID declarationId = UUID.randomUUID();

        when(declarationRepository.existsById(declarationId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            declarationService.deleteDeclaration(declarationId);
        });

        verify(declarationRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_ShouldNotRemoveDeclarationForMinor() {
        UUID patientId = UUID.randomUUID();
        
        Patient minorPatient = new Patient();
        minorPatient.setId(patientId);
        minorPatient.setBirthDate(LocalDate.now().minusYears(10));

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(minorPatient));

        declarationService.removeDeclarationsForAdultPatients();

        verify(declarationRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_ShouldRemoveDeclarationForAdultWithChildDoctor() {
        UUID patientId = UUID.randomUUID();
        UUID declarationId = UUID.randomUUID();
        
        Patient adultPatient = new Patient();
        adultPatient.setId(patientId);
        adultPatient.setBirthDate(LocalDate.now().minusYears(20));

        Doctor childDoctor = new Doctor();
        childDoctor.setType("child");

        Declaration declaration = new Declaration();
        declaration.setId(declarationId);
        declaration.setDoctor(childDoctor);
        declaration.setPatient(adultPatient);

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(adultPatient));
        when(declarationRepository.findById(patientId)).thenReturn(Optional.of(declaration));
        when(declarationRepository.existsById(declarationId)).thenReturn(true);

        declarationService.removeDeclarationsForAdultPatients();

        verify(declarationRepository, times(1)).deleteById(declarationId);
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_ShouldNotRemoveDeclarationForAdultWithAdultDoctor() {
        UUID patientId = UUID.randomUUID();
        
        Patient adultPatient = new Patient();
        adultPatient.setId(patientId);
        adultPatient.setBirthDate(LocalDate.now().minusYears(20));

        Doctor adultDoctor = new Doctor();
        adultDoctor.setType("adult");

        Declaration declaration = new Declaration();
        declaration.setDoctor(adultDoctor);
        declaration.setPatient(adultPatient);

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(adultPatient));
        when(declarationRepository.findById(patientId)).thenReturn(Optional.of(declaration));

        declarationService.removeDeclarationsForAdultPatients();

        verify(declarationRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_ShouldHandleEmptyPatientList() {
        when(patientRepository.findAll()).thenReturn(Collections.emptyList());

        declarationService.removeDeclarationsForAdultPatients();

        verify(declarationRepository, never()).findById(any(UUID.class));
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_ShouldHandlePatientWithoutDeclaration() {
        UUID patientId = UUID.randomUUID();
        
        Patient adultPatient = new Patient();
        adultPatient.setId(patientId);
        adultPatient.setBirthDate(LocalDate.now().minusYears(20));

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(adultPatient));
        when(declarationRepository.findById(patientId)).thenReturn(Optional.empty());

        declarationService.removeDeclarationsForAdultPatients();

        verify(declarationRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_BirthdayNotYetThisYear() {
        UUID patientId = UUID.randomUUID();
        UUID declarationId = UUID.randomUUID();
        
        // Birthday is later this year - should reduce calculated age by 1
        LocalDate today = LocalDate.now();
        LocalDate birthdayLaterThisYear = today.minusYears(18).plusDays(10);
        
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setBirthDate(birthdayLaterThisYear);

        Doctor childDoctor = new Doctor();
        childDoctor.setType("child");

        Declaration declaration = new Declaration();
        declaration.setId(declarationId);
        declaration.setDoctor(childDoctor);
        declaration.setPatient(patient);

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));
        when(declarationRepository.findById(patientId)).thenReturn(Optional.of(declaration));

        declarationService.removeDeclarationsForAdultPatients();

        // Age is 17 (birthday hasn't occurred yet), so should NOT remove declaration
        verify(declarationRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_ExactlyOnBirthday() {
        UUID patientId = UUID.randomUUID();
        UUID declarationId = UUID.randomUUID();
        
        // Today is exactly the 18th birthday
        LocalDate today = LocalDate.now();
        LocalDate exactBirthday = today.minusYears(18);
        
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setBirthDate(exactBirthday);

        Doctor childDoctor = new Doctor();
        childDoctor.setType("child");

        Declaration declaration = new Declaration();
        declaration.setId(declarationId);
        declaration.setDoctor(childDoctor);
        declaration.setPatient(patient);

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(patient));
        when(declarationRepository.findById(patientId)).thenReturn(Optional.of(declaration));
        when(declarationRepository.existsById(declarationId)).thenReturn(true);

        declarationService.removeDeclarationsForAdultPatients();

        // Age is exactly 18 on birthday, should remove declaration for child doctor
        verify(declarationRepository, times(1)).deleteById(declarationId);
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_MultiplePatients() {
        UUID patientId1 = UUID.randomUUID();
        UUID patientId2 = UUID.randomUUID();
        UUID declarationId1 = UUID.randomUUID();
        
        Patient minorPatient = new Patient();
        minorPatient.setId(patientId1);
        minorPatient.setBirthDate(LocalDate.now().minusYears(10));

        Patient adultPatient = new Patient();
        adultPatient.setId(patientId2);
        adultPatient.setBirthDate(LocalDate.now().minusYears(25));

        Doctor childDoctor = new Doctor();
        childDoctor.setType("child");

        Declaration declaration = new Declaration();
        declaration.setId(declarationId1);
        declaration.setDoctor(childDoctor);
        declaration.setPatient(adultPatient);

        when(patientRepository.findAll()).thenReturn(Arrays.asList(minorPatient, adultPatient));
        when(declarationRepository.findById(patientId1)).thenReturn(Optional.empty());
        when(declarationRepository.findById(patientId2)).thenReturn(Optional.of(declaration));
        when(declarationRepository.existsById(declarationId1)).thenReturn(true);

        declarationService.removeDeclarationsForAdultPatients();

        // Only adult patient's declaration should be removed
        verify(declarationRepository, times(1)).deleteById(declarationId1);
    }

    @Test
    public void testRemoveDeclarationsForAdultPatients_NullDoctorType() {
        UUID patientId = UUID.randomUUID();
        
        Patient adultPatient = new Patient();
        adultPatient.setId(patientId);
        adultPatient.setBirthDate(LocalDate.now().minusYears(20));

        Doctor doctor = new Doctor();
        doctor.setType(null);

        Declaration declaration = new Declaration();
        declaration.setDoctor(doctor);
        declaration.setPatient(adultPatient);

        when(patientRepository.findAll()).thenReturn(Collections.singletonList(adultPatient));
        when(declarationRepository.findById(patientId)).thenReturn(Optional.of(declaration));

        declarationService.removeDeclarationsForAdultPatients();

        // Type is not "child", so should not remove
        verify(declarationRepository, never()).deleteById(any(UUID.class));
    }
}

