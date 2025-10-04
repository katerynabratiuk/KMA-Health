package kma.health.app.kma_health.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PatientDto {
    private String passportNumber;
    private String email;
    private String phoneNumber;
    private String fullName;
    private LocalDate birthDate;

    public PatientDto(kma.health.app.kma_health.entity.Patient patient) {
        this.passportNumber = patient.getPassportNumber();
        this.email = patient.getEmail();
        this.phoneNumber = patient.getPhoneNumber();
        this.fullName = patient.getFullName();
        this.birthDate = patient.getBirthDate();
    }
}

