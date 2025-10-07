package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.entity.Declaration;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.exception.DoctorTypeMismatchException;
import kma.health.app.kma_health.repository.DeclarationRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DeclarationService {

    private final DeclarationRepository declarationRepository;
    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;

    public void createDeclaration(Doctor doctor, Patient patient) {
        if (!doctor.getDoctorType().getTypeName().equals("Family"))
            throw new DoctorTypeMismatchException("Cannot create declaration of a doctor of type: " + doctor.getDoctorType().getTypeName());
        appointmentService.validateDoctorAndPatientAge(doctor.getId(), patient.getId());
        Declaration declaration = new Declaration();
        declaration.setDoctor(doctor);
        declaration.setPatient(patient);
        declaration.setDateSigned(LocalDate.now());
        declarationRepository.save(declaration);
    }

    public void deleteDeclaration(UUID id) {
        if (declarationRepository.existsById(id))
            declarationRepository.deleteById(id);
        else throw new EntityNotFoundException("Declaration not found");
    }

    @Scheduled(cron = "0 1 0 * * *")
    public void removeDeclarationsForAdultPatients() {
        List<Patient> patients = patientRepository.findAll();

        LocalDate today = LocalDate.now();
        for (Patient patient : patients) {
            LocalDate birthDate = patient.getBirthDate();

            int calculatedAge = today.getYear() - birthDate.getYear();
            if (birthDate.plusYears(calculatedAge).isAfter(today))
                calculatedAge--;
            final int age = calculatedAge;

            declarationRepository.findById(patient.getId())
                    .ifPresent(declaration -> {
                        if (age >= 18 && "child".equals(declaration.getDoctor().getType()))
                            deleteDeclaration(declaration.getId());
                    });
        }
    }
}
