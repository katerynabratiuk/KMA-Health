package kma.health.app.kma_health.dto;

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

    private UserRole role;
    private LoginMethod method;
    private String identifier;
    private String password;
}
