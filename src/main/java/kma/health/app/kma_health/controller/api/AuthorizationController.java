package kma.health.app.kma_health.controller.api;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kma.health.app.kma_health.dto.LoginRequest;
import kma.health.app.kma_health.dto.DoctorRegisterRequest;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthorizationController {

    private final AuthService authService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        String token = switch (request.getMethod()) {
            case EMAIL -> authService.loginByEmail(request.getIdentifier(), request.getPassword(), request.getRole());
            case PHONE -> authService.loginByPhone(request.getIdentifier(), request.getPassword(), request.getRole());
            case PASSPORT -> authService.loginByPassport(request.getIdentifier(), request.getPassword(), request.getRole());
        };

        String userRole = request.getRole().name();

        ResponseCookie jwtCookie = ResponseCookie.from("JWT", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        ResponseCookie roleCookie = ResponseCookie.from("UserRole", userRole)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, roleCookie.toString());

        return ResponseEntity.ok(Map.of(
                "role", request.getRole(),
                "message", "Successfully logged in"
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody DoctorRegisterRequest request) {
        return ResponseEntity.ok(registrationService.register(request));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@AuthenticationPrincipal UUID userId,
                                                @RequestBody Map<String, String> updates) {
        authService.updateProfile(userId, updates);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(@AuthenticationPrincipal UUID userId) {
        authService.deleteProfile(userId);
        return ResponseEntity.ok("Profile deleted successfully");
    }
}

