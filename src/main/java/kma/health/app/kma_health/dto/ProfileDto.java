package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.LabAssistant;
import kma.health.app.kma_health.entity.Patient;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Data
public class ProfileDto {
    private UUID profileId;
    private String fullName;
    private LocalDate dateOfBirth;
    private String email;
    private String phoneNumber;
    private String passportNumber;
    private String profilePictureUrl;

    private String description;
    private String type;
    private String doctorType;
    private String hospitalName;
    private String patientType;
    private String familyDoctorName;

    private List<AppointmentShortViewDto> plannedAppointments;

    public ProfileDto(Doctor doctor) {
        this.profileId = doctor.getId();
        this.fullName = doctor.getFullName();
        this.dateOfBirth = doctor.getBirthDate();
        this.email = doctor.getEmail();
        this.phoneNumber = doctor.getPhoneNumber();
        this.passportNumber = doctor.getPassportNumber();
        this.profilePictureUrl = doctor.getProfilePictureUrl();
        this.description = doctor.getDescription();
        this.type = doctor.getType();
        this.doctorType = doctor.getDoctorType().getTypeName();
        this.hospitalName = doctor.getHospital().getName();
    }

    public ProfileDto(Patient patient) {
        this.profileId = patient.getId();
        this.fullName = patient.getFullName();
        this.dateOfBirth = patient.getBirthDate();
        this.email = patient.getEmail();
        this.phoneNumber = patient.getPhoneNumber();
        this.passportNumber = patient.getPassportNumber();
        this.profilePictureUrl = patient.getProfilePictureUrl();

        if (patient.getBirthDate() != null) {
            int age = Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
            this.patientType = age < 18 ? "child" : "adult";
        } else {
            this.patientType = "N/A";
        }
    }

    public ProfileDto(LabAssistant labAssistant) {
        this.profileId = labAssistant.getId();
        this.fullName = labAssistant.getFullName();
        this.dateOfBirth = labAssistant.getBirthDate();
        this.email = labAssistant.getEmail();
        this.phoneNumber = labAssistant.getPhoneNumber();
        this.passportNumber = labAssistant.getPassportNumber();
        this.profilePictureUrl = labAssistant.getProfilePictureUrl();
        this.hospitalName = labAssistant.getHospital().getName();
    }
}