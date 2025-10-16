package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.AuthUser;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.repository.AuthUserRepository;
import kma.health.app.kma_health.security.JwtUtils;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final Map<UserRole, AuthUserRepository<? extends AuthUser>> repositories;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final Marker SECURITY = MarkerFactory.getMarker("SECURITY");

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
        MDC.put("userRole", role.toString());

        try {
            AuthUser user = finder.apply(repo)
                    .orElseThrow(() -> {
                        MDC.put("status", "FAILED");
                        MDC.put("reason", "User not found");
                        log.warn(SECURITY, "Failed login: user with role {} not found", role);
                        return new RuntimeException(role + " not found");
                    });

            MDC.put("userId", String.valueOf(user.getId()));

            if (!passwordEncoder.matches(password, user.getPassword())) {
                MDC.put("status", "FAILED");
                MDC.put("reason", "Invalid password");
                log.warn(SECURITY, "Failed login attempt for user ID: {} (Role: {}). Invalid password.", user.getId(), role);
                throw new RuntimeException("Invalid credentials");
            }

            MDC.put("status", "SUCCESS");
            log.info(SECURITY, "Successful login for user ID: {} (Role: {}).", user.getId(), role);
            return jwtUtils.generateToken(user);

        } catch (RuntimeException e) {
            if (!MDC.getCopyOfContextMap().containsKey("status")) {
                MDC.put("status", "FAILED");
                MDC.put("reason", e.getMessage());
            }
            throw e;
        } finally {
            MDC.clear();
        }
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

        UUID subject = jwtUtils.getSubjectFromToken(token);
        T user = repo.findById(subject)
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
        UUID subject = jwtUtils.getSubjectFromToken(token);
        UserRole role = jwtUtils.getRoleFromToken(token);

        AuthUserRepository<T> repo = (AuthUserRepository<T>) getRepositoryByRole(role);
        T user = repo.findById(subject)
                .orElseThrow(() -> new RuntimeException(role + " not found"));

        repo.delete(user);
    }

    public ResponseEntity<String> validateAuthorizationHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }
        return null;
    }

    public String extractToken(String authHeader) {
        return authHeader.substring(7);
    }

    public AuthUser getUserFromToken(String token) {
        UserRole role = jwtUtils.getRoleFromToken(token);
        UUID id = jwtUtils.getSubjectFromToken(token);

        return switch (role) {
            case UserRole.PATIENT -> repositories.get(UserRole.PATIENT).getReferenceById(id);
            case UserRole.DOCTOR -> repositories.get(UserRole.DOCTOR).getReferenceById(id);
            default -> throw new RuntimeException("Couldn't find " + role);
        };
    }
}

