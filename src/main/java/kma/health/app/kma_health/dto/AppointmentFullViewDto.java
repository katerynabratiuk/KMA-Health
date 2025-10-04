package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Appointment;
import kma.health.app.kma_health.entity.MedicalFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private String doctorId;

    private String diagnosis;

    private Long hospitalId;
    private String hospitalName;

    private UUID referralId;

    private Set<MedicalFileDto> medicalFiles;

    public AppointmentFullViewDto(Appointment appointment)
    {
        this.id = appointment.getId();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.doctorId = appointment.getDoctor().getPassportNumber();
        this.doctorName = appointment.getDoctor().getFullName();
        this.diagnosis = appointment.getDiagnosis();
        this.hospitalId = appointment.getDoctor().getHospital().getId();
        this.hospitalName = appointment.getDoctor().getHospital().getName();
        this.referralId = appointment.getReferral().getId();
        this.medicalFiles = (appointment.getMedicalFiles() == null) ? Set.of()
                : appointment.getMedicalFiles().stream()
                .map(MedicalFileDto::new)
                .collect(Collectors.toSet());
    }
}
