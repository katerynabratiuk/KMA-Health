package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.PatientContactsDto;
import kma.health.app.kma_health.dto.PatientDto;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;
    private final AuthService authService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public PatientDto getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authService.extractToken(authHeader);
        UUID id = authService.getUserFromToken(token).getId();
        return new PatientDto(patientService.getPatientById(id));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/contacts")
    public ResponseEntity<PatientContactsDto> getPatientContacts(UUID patientId) {
        try {
            PatientContactsDto patientContacts = patientService.getPatientContacts(patientId);
            return ResponseEntity.ok(patientContacts);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
