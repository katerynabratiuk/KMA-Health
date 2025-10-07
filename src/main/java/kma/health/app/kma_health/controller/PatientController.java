package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.PatientDto;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AuthService authService;

    @GetMapping("/profile")
    public PatientDto getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authService.extractToken(authHeader);
        UUID id = authService.getUserFromToken(token).getId();
        return new PatientDto(patientService.getPatientById(id));
    }

}
