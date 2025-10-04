package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.Declaration;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PatientService{

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;


    public Patient getPatientById(String id) {
        return patientRepository.getReferenceById(id);
    }

    public List<Appointment> getAppointments(String patientId) {
        return appointmentRepository.findAllByReferralPatientPassportNumber(patientId);
    }

    public List<Declaration> getDeclarations(String patientId) {
        return List.of();
    }

}