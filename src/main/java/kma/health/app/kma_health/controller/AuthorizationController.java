package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.LoginRequest;
import kma.health.app.kma_health.dto.AuthResponse;
import kma.health.app.kma_health.service.AuthService;
import jakarta.validation.Valid;
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
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request.login(), request.password(), request.role());
        return new AuthResponse(token);
    }

    @PatchMapping("/profile")
    public String updateProfile(@RequestHeader("Authorization") String authHeader,
                                @RequestBody Map<String, String> updates) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");

        String token = authHeader.substring(7);
        authService.updateProfile(token, updates);
        return "Profile updated successfully";
    }

    @DeleteMapping("/profile")
    public String deleteProfile(@RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new RuntimeException("Missing or invalid Authorization header");

        String token = authHeader.substring(7);
        authService.deleteProfile(token);
        return "Profile deleted successfully";
    }
}
