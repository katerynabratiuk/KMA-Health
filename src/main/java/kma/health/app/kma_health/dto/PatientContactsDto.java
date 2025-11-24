package kma.health.app.kma_health.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
public class PatientContactsDto {
    private String fullName;
    private String phone;
    private String email;
    private String familyDoctorName;
    private LocalDate birthDate;
}
