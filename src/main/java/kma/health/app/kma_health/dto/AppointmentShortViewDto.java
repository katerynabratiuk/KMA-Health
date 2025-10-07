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
    private UUID patientId;

    public AppointmentShortViewDto(Appointment appointment) {
        this.id = appointment.getId();
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.doctorId = appointment.getDoctor().getId();
        this.hospitalId = appointment.getHospital().getId();
        this.patientId = appointment.getReferral().getPatient().getId();
        this.doctorName = appointment.getDoctor().getFullName();
    }
}