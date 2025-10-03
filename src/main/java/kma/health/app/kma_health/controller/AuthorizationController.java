package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.LoginRequest;
import kma.health.app.kma_health.dto.RegisterRequest;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthorizationController {

    private final AuthService authService;
    private final RegistrationService registrationService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token;
        switch (request.getMethod()) {
            case EMAIL -> token = authService.loginByEmail(request.getIdentifier(), request.getPassword(), request.getRole());
            case PHONE -> token = authService.loginByPhone(request.getIdentifier(), request.getPassword(), request.getRole());
            case PASSPORT -> token = authService.loginByPassport(request.getIdentifier(), request.getPassword(), request.getRole());
            default -> throw new IllegalArgumentException("Unknown login method");
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = registrationService.register(request);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody Map<String, String> updates) {

        ResponseEntity<String> validationResponse = authService.validateAuthorizationHeader(authHeader);
        if (validationResponse != null) return validationResponse;

        String token = authService.extractToken(authHeader);
        authService.updateProfile(token, updates);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(@RequestHeader("Authorization") String authHeader) {

        ResponseEntity<String> validationResponse = authService.validateAuthorizationHeader(authHeader);
        if (validationResponse != null) return validationResponse;

        String token = authService.extractToken(authHeader);
        authService.deleteProfile(token);
        return ResponseEntity.ok("Profile deleted successfully");
    }
}
