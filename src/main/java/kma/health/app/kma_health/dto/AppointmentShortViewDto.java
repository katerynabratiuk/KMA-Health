package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentShortViewDto {
    private UUID id;
    private LocalDate date;
    private LocalTime time;
    private String doctorName;
    private UUID doctorId;
    private Long hospitalId;
    private String examinationName;

    public AppointmentShortViewDto(Appointment appointment) {
        this.id = appointment.getId();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.doctorId = (appointment.getDoctor() != null) ? appointment.getDoctor().getId() : null;
        this.hospitalId = (appointment.getHospital() != null) ? appointment.getHospital().getId() : null;
        this.doctorName = (appointment.getDoctor() != null) ? appointment.getDoctor().getFullName() : null;
        this.examinationName = (appointment.getReferral().getExamination() != null)
                ? appointment.getReferral().getExamination().getExamName()
                : null;
    }
}