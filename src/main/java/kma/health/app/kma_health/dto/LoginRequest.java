package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kma.health.app.kma_health.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    public enum LoginMethod {
        EMAIL,
        PHONE,
        PASSPORT
    }

    @NotNull(message = "User role is required")
    private UserRole role;

    @NotNull(message = "Login method is required")
    private LoginMethod method;

    @NotBlank(message = "Identifier cannot be blank")
    @Size(max = 100, message = "Identifier is too long (maximum 100 characters)")
    private String identifier;

    @NotBlank(message = "Password is required")
    @Size(min = 2, max = 50, message = "Password must be between 2 and 50 characters")
    private String password;
}
