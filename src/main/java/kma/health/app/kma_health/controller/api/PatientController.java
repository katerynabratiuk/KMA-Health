package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.PatientContactsDto;
import kma.health.app.kma_health.dto.PatientDto;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/history")
    public ResponseEntity<List<AppointmentFullViewDto>> getPatientHistory(@RequestHeader("Authorization") String authHeader,
                                                                          @RequestParam UUID patientId) {
        JwtUtils jwtUtils = new JwtUtils();
        UserRole role = jwtUtils.getRoleFromToken(authService.extractToken(authHeader));
        switch (role) {
            case PATIENT -> {
                return ResponseEntity.ok(patientService.getPatientMedicalHistory(patientId, null, UserRole.PATIENT));
            }
            case DOCTOR -> {
                try {
                    UUID doctorId = authService.getUserFromToken(authService.extractToken(authHeader)).getId();
                    return ResponseEntity.ok(patientService.getPatientMedicalHistory(patientId, doctorId, UserRole.DOCTOR));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            default -> {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }

    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/referrals")
    public ResponseEntity<List<ReferralDto>> getReferrals(@RequestHeader("Authorization") String authHeader,
                                                          @RequestParam UUID patientId) {
        JwtUtils jwtUtils = new JwtUtils();
        UserRole role = jwtUtils.getRoleFromToken(authService.extractToken(authHeader));
        switch (role) {
            case PATIENT -> {
                return ResponseEntity.ok(patientService.getPatientReferrals(patientId, null, role));
            }
            case DOCTOR -> {
                try {
                    UUID doctorId = authService.getUserFromToken(authService.extractToken(authHeader)).getId();
                    return ResponseEntity.ok(patientService.getPatientReferrals(patientId, doctorId, role));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            default -> {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
    }
}
