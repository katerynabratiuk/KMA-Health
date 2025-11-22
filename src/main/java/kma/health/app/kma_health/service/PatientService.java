package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.PatientContactsDto;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.entity.*;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.PatientHistoryAccessException;
import kma.health.app.kma_health.repository.AppointmentRepository;
import kma.health.app.kma_health.repository.DeclarationRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final DeclarationRepository declarationRepository;
    private final AppointmentService appointmentService;
    private final ReferralService referralService;

    public Patient getPatientById(UUID id) {
        return patientRepository.getReferenceById(id);
    }

    public List<Appointment> getAppointments(UUID patientId) {
        return appointmentRepository.findByReferral_Patient_Id(patientId);
    }

    public Declaration getDeclaration(UUID patientId) {
        return declarationRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Declaration not found"));
    }

    public PatientContactsDto getPatientContacts(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient " + patientId + " not found"));

        Doctor doctor = declarationRepository.findByPatientId(patientId)
                .map(Declaration::getDoctor)
                .orElse(null);

        PatientContactsDto dto = new PatientContactsDto();
        dto.setFullName(patient.getFullName());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhoneNumber());
        dto.setFamilyDoctorName(doctor != null ? doctor.getFullName() : null);

        return dto;
    }

    public List<AppointmentFullViewDto> getPatientMedicalHistory(UUID patientId, UUID doctorId, UserRole role) {
        return switch (role) {
            case PATIENT -> appointmentService.getAppointmentsForPatient(patientId);
            case DOCTOR -> {
                if (!appointmentService.haveOpenAppointment(doctorId, patientId)) {
                    throw new PatientHistoryAccessException(
                            "Patient " + patientId + " has no open appointment with doctor " + doctorId
                    );
                }
                yield appointmentService.getAppointmentsForPatient(patientId);
            }
            default -> throw new PatientHistoryAccessException(
                    "Denied medical history access to " + role
            );
        };
    }

    public List<ReferralDto> getPatientReferrals(UUID patientId, UUID doctorId, UserRole role) {
        List<Referral> referrals = switch (role) {
            case PATIENT -> referralService.getAllReferrals(patientId);
            case DOCTOR -> {
                if (!appointmentService.haveOpenAppointment(doctorId, patientId)) {
                    throw new PatientHistoryAccessException(
                            "Patient " + patientId + " has no open appointment with doctor " + doctorId
                    );
                }
                yield referralService.getAllReferrals(patientId);
            }
            default -> throw new PatientHistoryAccessException("Denied medical history access to " + role);
        };
        return referrals.stream()
                .map(ReferralDto::fromEntity)
                .toList();
    }
}