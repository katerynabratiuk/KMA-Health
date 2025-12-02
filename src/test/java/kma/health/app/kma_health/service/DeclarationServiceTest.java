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
}

