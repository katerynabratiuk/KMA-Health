package kma.health.app.kma_health.dto;

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
public class AppointmentCreateUpdateDto {
    private LocalDate date;
    private LocalTime time;
    private UUID doctorId;
    private UUID patientId;
    private String diagnosis;
    private Long hospitalId;
    private UUID referralId;
    private UUID labAssistantId;
}
