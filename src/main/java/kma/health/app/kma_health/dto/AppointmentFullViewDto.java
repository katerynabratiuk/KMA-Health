package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.entity.Referral;
import kma.health.app.kma_health.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentFullViewDto {
    private UUID id;
    private LocalDate date;
    private LocalTime time;

    private String doctorName;
    private String doctorType;
    private String doctorPhoto;
    private UUID doctorId;

    private String referralDoctorName;
    private String referralDoctorType;
    private String referralDoctorPhoto;

    private UUID patientId;
    private String patientName;
    private String patientPhoto;

    private String diagnosis;
    private AppointmentStatus status;

    private Long hospitalId;
    private String hospitalName;
    private String examinationName;

    private UUID referralId;

    private Set<MedicalFileDto> medicalFiles;

    public AppointmentFullViewDto(Appointment appointment) {
        this.id = appointment.getId();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.diagnosis = appointment.getDiagnosis();
        this.status = appointment.getStatus();
        this.medicalFiles = (appointment.getMedicalFiles() == null) ? Set.of()
                : appointment.getMedicalFiles().stream()
                .map(MedicalFileDto::new)
                .collect(Collectors.toSet());

        Doctor doctor = appointment.getDoctor();
        Referral referral = appointment.getReferral();
        Doctor referralDoctor = (referral != null && referral.getDoctor() != null) ? referral.getDoctor() : null;
        Patient patient = (referral != null && referral.getPatient() != null) ? referral.getPatient() : null;


        this.doctorId = (doctor != null) ? doctor.getId() : null;
        this.doctorName = (doctor != null) ? doctor.getFullName() : null;
        this.doctorPhoto = (doctor != null) ? doctor.getProfilePictureUrl() : null;
        this.doctorType = (doctor != null && doctor.getDoctorType() != null)
                ? doctor.getDoctorType().getTypeName()
                : null;

        this.hospitalId = appointment.getHospital() != null
                ? appointment.getHospital().getId()
                : null;
        this.hospitalName = appointment.getHospital() != null
                ? appointment.getHospital().getName()
                : null;

        this.referralId = (referral != null) ? referral.getId() : null;
        this.patientId = (patient != null) ? patient.getId() : null;
        this.patientName = (patient != null) ? patient.getFullName() : null;
        this.patientPhoto = (patient != null) ? patient.getProfilePictureUrl() : null;

        this.examinationName = (referral != null && referral.getExamination() != null)
                ? referral.getExamination().getExamName()
                : null;

        this.referralDoctorName = (referralDoctor != null) ? referralDoctor.getFullName() : null;
        this.referralDoctorPhoto = (referralDoctor != null) ? referralDoctor.getProfilePictureUrl() : null;
        this.referralDoctorType = (referralDoctor != null && referralDoctor.getDoctorType() != null)
                ? referralDoctor.getDoctorType().getTypeName()
                : null;
    }
}
