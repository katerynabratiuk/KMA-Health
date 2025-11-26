package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.CreateReferralRequest;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.service.AuthService;
import kma.health.app.kma_health.service.DoctorSearchService;
import kma.health.app.kma_health.service.PatientService;
import kma.health.app.kma_health.service.ReferralService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/referral")
public class ReferralController {

    private final AuthService authService;
    private final ReferralService referralService;
    private final PatientService patientService;
    private final DoctorSearchService doctorSearchService;

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<?> createReferral(
            @AuthenticationPrincipal UUID userId,
            @RequestBody CreateReferralRequest request
    ) {
        Doctor doctor = doctorSearchService.getDoctorById(userId);
        if (doctor == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        Patient patient = patientService.getPatientById(request.getPatientId());

        if (request.getDoctorTypeName() == null || request.getDoctorTypeName().isEmpty())
            referralService.createReferral(doctor, patient, request.getExaminationId());
        else
            referralService.createReferral(doctor, patient, request.getDoctorTypeName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
