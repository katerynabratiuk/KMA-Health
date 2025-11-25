package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotNull(message="Номер паспорту є обов'язковим полем")
    @Size(min=9, max=9, message = "Розмір номеру паспорту - 9 цифр")
    @Pattern(regexp="[0-9]{9}", message = "Номер паспорту може складатись лише з цифр")
    private String passportNumber;

    @NotNull(message="Пошта є обов'язковим полем")
    @Email(message="Неправильний формат пошти")
    private String email;

    @NotNull(message="Номер паспорту є обов'язковим полем")
    private String password;

    @NotNull(message="Номер телефону є обов'язковим полем")
    private String phoneNumber;

    @NotNull(message="ПІБ є обов'язковим полем")
    private String fullName;

    @NotNull(message="Дата народження є обов'язковим полем")
    private LocalDate birthDate;

    private LocalDate startedWorking;

    private String type;
    private Long doctorTypeId;
    private Long hospitalId;
    private String description;

    private Long labHospitalId;

    private String registerKey;
}
