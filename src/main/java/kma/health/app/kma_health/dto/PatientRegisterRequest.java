package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.validator.UniqueCredential;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRegisterRequest {
    private UserRole role;

    @NotNull(message = "Номер паспорту є обов'язковим полем")
    @Size(min = 9, max = 9, message = "Розмір номеру паспорту - 9 цифр")
    @Pattern(regexp = "[0-9]{9}", message = "Номер паспорту може складатись лише з цифр")
    @UniqueCredential(field = "passportNumber", message = "Користувач з таким номером паспорту вже існує")
    private String passportNumber;

    @NotNull(message = "Пошта є обов'язковим полем")
    @Email(message = "Неправильний формат пошти")
    @UniqueCredential(field = "email", message = "Користувач з такою поштою вже існує")
    private String email;

    @NotNull(message = "Номер паспорту є обов'язковим полем")
    private String password;

    @NotNull(message = "Номер телефону є обов'язковим полем")
    @UniqueCredential(field = "phoneNumber", message = "Користувач з таким номером телефону вже існує")
    private String phoneNumber;

    @NotNull(message = "ПІБ є обов'язковим полем")
    private String fullName;

    @NotNull(message = "Дата народження є обов'язковим полем")
    private LocalDate birthDate;
}
