package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.CreateReferralRequest;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.PatientService;
import kma.health.app.kma_health.service.ReferralService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/referral")
public class ReferralController {

    private final AuthService authService;
    private final ReferralService referralService;
    private final PatientService patientService;

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<?> createReferral(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam CreateReferralRequest request
    ) {
        String token = authService.extractToken(authHeader);
        Doctor doctor = (Doctor) authService.getUserFromToken(token);
        Patient patient = patientService.getPatientById(request.getPatientId());

        if (request.getDoctorTypeName() == null || request.getDoctorTypeName().isEmpty())
            referralService.createReferral(doctor, patient, request.getExaminationId());
        else
            referralService.createReferral(doctor, patient, request.getDoctorTypeName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
