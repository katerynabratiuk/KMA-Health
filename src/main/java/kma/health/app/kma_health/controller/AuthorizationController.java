package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.LoginRequest;
import kma.health.app.kma_health.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final AuthService authService;

    public AuthorizationController(AuthService authService) {
        this.authService = authService;
    }

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

    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestHeader("Authorization") String authHeader,
                                                @RequestBody Map<String, String> updates) {

        ResponseEntity<String> validationResponse = validateAuthorizationHeader(authHeader);
        if (validationResponse != null) return validationResponse;

        String token = extractToken(authHeader);
        authService.updateProfile(token, updates);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(@RequestHeader("Authorization") String authHeader) {

        ResponseEntity<String> validationResponse = validateAuthorizationHeader(authHeader);
        if (validationResponse != null) return validationResponse;

        String token = extractToken(authHeader);
        authService.deleteProfile(token);
        return ResponseEntity.ok("Profile deleted successfully");
    }

    private ResponseEntity<String> validateAuthorizationHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header");
        }
        return null;
    }

    private String extractToken(String authHeader) {
        return authHeader.substring(7);
    }
}
