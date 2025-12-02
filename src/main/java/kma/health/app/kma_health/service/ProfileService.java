package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.dto.ProfileDto;
import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.LabAssistant;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.repository.DoctorRepository;
import kma.health.app.kma_health.repository.LabAssistantRepository;
import kma.health.app.kma_health.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final LabAssistantRepository labAssistantRepository;
    private final PatientService patientService;

    public ProfileDto getProfileData(UUID userId, String userRole) {
        return switch (userRole) {
            case "DOCTOR" -> getDoctorProfile(userId);
            case "PATIENT" -> getPatientProfile(userId);
            case "LAB_ASSISTANT" -> getLabAssistantProfile(userId);
            default -> throw new EntityNotFoundException("User role not supported or user not found.");
        };
    }

    private ProfileDto getDoctorProfile(UUID userId) {
        Doctor doctor = doctorRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with id: " + userId));
        return new ProfileDto(doctor);
    }

    private ProfileDto getPatientProfile(UUID userId) {
        Patient patient = patientRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id: " + userId));

        ProfileDto dto = new ProfileDto(patient);
        try {
            Doctor familyDoctor = patientService.getFamilyDoctor(userId);
            if (familyDoctor != null) {
                dto.setFamilyDoctorName(familyDoctor.getFullName());
                dto.setFamilyDoctorId(familyDoctor.getId());
            } else {
                dto.setFamilyDoctorName(null);
                dto.setFamilyDoctorId(null);
            }
        } catch (EntityNotFoundException e) {
            dto.setFamilyDoctorName(null);
            dto.setFamilyDoctorId(null);
        }

        List<Appointment> scheduledAppointments = patientService.getScheduledAppointments(userId);
        List<AppointmentShortViewDto> plannedAppointmentsDto = scheduledAppointments.stream()
                .map(AppointmentShortViewDto::new)
                .collect(Collectors.toList());

        dto.setPlannedAppointments(plannedAppointmentsDto);

        return dto;
    }

    private ProfileDto getLabAssistantProfile(UUID userId) {
        LabAssistant labAssistant = labAssistantRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Lab Assistant not found with id: " + userId));
        return new ProfileDto(labAssistant);
    }
}