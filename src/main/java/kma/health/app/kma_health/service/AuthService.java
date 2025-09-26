package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.AuthUser;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.repository.AuthUserRepository;
import kma.health.app.kma_health.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final Map<UserRole, AuthUserRepository<? extends AuthUser>> repositories;

    public AuthService(
            Map<UserRole, AuthUserRepository<? extends AuthUser>> repositories,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils
    ) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.repositories = repositories;
    }

    private AuthUserRepository<? extends AuthUser> getRepositoryByRole(UserRole role) {
        return Optional.ofNullable(repositories.get(role))
                .orElseThrow(() -> new RuntimeException("Repository for role " + role + " not found"));
    }

    private String login(Function<AuthUserRepository<?>, Optional<? extends AuthUser>> finder,
                         String password,
                         UserRole role) {

        AuthUserRepository<? extends AuthUser> repo = getRepositoryByRole(role);

        AuthUser user = finder.apply(repo)
                .orElseThrow(() -> new RuntimeException(role + " not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtils.generateToken(user);
    }

    public String loginByEmail(String email, String password, UserRole role) {
        return login(r -> r.findByEmail(email), password, role);
    }

    public String loginByPhone(String phone, String password, UserRole role) {
        return login(r -> r.findByPhoneNumber(phone), password, role);
    }

    public String loginByPassport(String passport, String password, UserRole role) {
        return login(r -> r.findByPassportNumber(passport), password, role);
    }

    @SuppressWarnings("unchecked")
    public <T extends AuthUser> void updateProfile(String token, Map<String, String> updates) {
        UserRole role = jwtUtils.getRoleFromToken(token);
        AuthUserRepository<T> repo = (AuthUserRepository<T>) getRepositoryByRole(role);

        String subject = jwtUtils.getSubjectFromToken(token);
        T user = repo.findByPassportNumber(subject)
                .orElseThrow(() -> new RuntimeException(role + " not found"));

        applyUpdates(user, updates);
        repo.save(user);
    }

    private void applyUpdates(AuthUser user, Map<String, String> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "email" -> user.setEmail(value);
                case "password" -> user.setPassword(passwordEncoder.encode(value));
                case "phoneNumber" -> user.setPhoneNumber(value);
                case "passport" -> user.setPassportNumber(value);
                default -> throw new RuntimeException("Unknown field: " + key);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends AuthUser> void deleteProfile(String token) {
        String subject = jwtUtils.getSubjectFromToken(token);
        UserRole role = jwtUtils.getRoleFromToken(token);

        AuthUserRepository<T> repo = (AuthUserRepository<T>) getRepositoryByRole(role);
        T user = repo.findByPassportNumber(subject)
                .orElseThrow(() -> new RuntimeException(role + " not found"));

        repo.delete(user);
    }
}

