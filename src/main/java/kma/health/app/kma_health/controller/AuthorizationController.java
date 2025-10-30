package kma.health.app.kma_health.controller;

import jakarta.validation.Valid;
import kma.health.app.kma_health.dto.LoginRequest;
import kma.health.app.kma_health.dto.RegisterRequest;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthorizationController {

    private final AuthService authService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        String token = switch (request.getMethod()) {
            case EMAIL -> authService.loginByEmail(request.getIdentifier(), request.getPassword(), request.getRole());
            case PHONE -> authService.loginByPhone(request.getIdentifier(), request.getPassword(), request.getRole());
            case PASSPORT -> authService.loginByPassport(request.getIdentifier(), request.getPassword(), request.getRole());
        };
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
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

