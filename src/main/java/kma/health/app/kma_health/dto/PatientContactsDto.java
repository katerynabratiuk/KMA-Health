package kma.health.app.kma_health.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PatientContactsDto {
    private String fullName;
    private String phone;
    private String email;
    private String familyDoctorName;
    private UUID familyDoctorId;
    private LocalDate birthDate;
}
