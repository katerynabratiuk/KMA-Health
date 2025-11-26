package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Appointment;
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

    private String diagnosis;
    private AppointmentStatus status;

    private Long hospitalId;
    private String hospitalName;
    private String examinationName;

    private UUID referralId;

    private Set<MedicalFileDto> medicalFiles;

    public AppointmentFullViewDto(Appointment appointment)
    {
        this.id = appointment.getId();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.doctorId = appointment.getDoctor().getId();
        this.patientId = appointment.getReferral().getPatient().getId();
        this.doctorType = appointment.getDoctor().getDoctorType().getTypeName();
        this.doctorName = appointment.getDoctor().getFullName();
        this.doctorPhoto = appointment.getDoctor().getProfilePictureUrl();
        this.referralDoctorName = appointment.getReferral().getDoctor().getFullName();
        this.referralDoctorType = appointment.getReferral().getDoctor().getDoctorType().getTypeName();
        this.referralDoctorPhoto = appointment.getReferral().getDoctor().getProfilePictureUrl();
        this.patientName = appointment.getReferral().getPatient().getFullName();
        this.diagnosis = appointment.getDiagnosis();
        this.status = appointment.getStatus();
        this.hospitalId = appointment.getDoctor().getHospital().getId();
        this.hospitalName = appointment.getDoctor().getHospital().getName();
        this.examinationName = appointment.getReferral().getExamination() != null ? appointment.getReferral().getExamination().getExamName() : null;
        this.referralId = appointment.getReferral().getId();
        this.medicalFiles = (appointment.getMedicalFiles() == null) ? Set.of()
                : appointment.getMedicalFiles().stream()
                .map(MedicalFileDto::new)
                .collect(Collectors.toSet());
    }
}
