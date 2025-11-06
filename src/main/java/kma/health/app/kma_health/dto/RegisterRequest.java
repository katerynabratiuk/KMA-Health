package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.enums.UserRole;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RegisterRequest {
    private UserRole role;

    private String passportNumber;
    private String email;
    private String password;
    private String phoneNumber;
    private String fullName;
    private LocalDate birthDate;

    private String type;
    private Long doctorTypeId;
    private Long hospitalId;
    private String description;

    private Long labHospitalId;

    private String registerKey;
}
