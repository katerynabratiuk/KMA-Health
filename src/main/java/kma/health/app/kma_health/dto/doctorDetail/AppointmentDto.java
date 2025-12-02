package kma.health.app.kma_health.dto.doctorDetail;

import kma.health.app.kma_health.entity.Appointment;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AppointmentDto { // short dto for calendar

    private LocalDate date;
    private LocalTime time;
    private UUID doctorId;
    private Long hospitalId;

    public AppointmentDto(Appointment appointment) {
        this.date = appointment.getDate();
        this.time = appointment.getTime();
        this.doctorId = (appointment.getDoctor() != null) ? appointment.getDoctor().getId() : null;
        this.hospitalId = (appointment.getHospital() != null) ? appointment.getHospital().getId() : null;
    }
}
