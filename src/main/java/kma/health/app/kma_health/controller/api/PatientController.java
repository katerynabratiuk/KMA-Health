package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.PatientContactsDto;
import kma.health.app.kma_health.dto.PatientDto;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public PatientDto getProfile(@AuthenticationPrincipal UUID userId) {
        return new PatientDto(patientService.getPatientById(userId));
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
    public ResponseEntity<List<AppointmentFullViewDto>> getPatientHistory(
            @AuthenticationPrincipal UUID userId,
            @RequestParam UUID patientId
    ) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPatient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
        boolean isDoctor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        if (isPatient) {
            if (!userId.equals(patientId))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            return ResponseEntity.ok(patientService.getPatientMedicalHistory(patientId, null, UserRole.PATIENT));
        } else if (isDoctor) {
            return ResponseEntity.ok(patientService.getPatientMedicalHistory(patientId, userId, UserRole.DOCTOR));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @GetMapping("/referrals")
    public ResponseEntity<List<ReferralDto>> getReferrals(
            @AuthenticationPrincipal UUID userId,
            @RequestParam UUID patientId
    ) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPatient = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
        boolean isDoctor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        if (isPatient) {
            if (!userId.equals(patientId))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            return ResponseEntity.ok(patientService.getPatientReferrals(patientId, null, UserRole.PATIENT));
        } else if (isDoctor) {
            return ResponseEntity.ok(patientService.getPatientReferrals(patientId, userId, UserRole.DOCTOR));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
