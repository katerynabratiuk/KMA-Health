package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.AppointmentCreateUpdateDto;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.dto.MedicalFileUploadDto;
import kma.health.app.kma_health.dto.doctorDetail.AppointmentDto;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import kma.health.app.kma_health.exception.ErrorResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static kma.health.app.kma_health.dto.MedicalFileUploadDto.convertMultipartFilesToDto;

@RestController
@RequestMapping("/api/appointments")
@AllArgsConstructor
public class AppointmentController {

    private final AuthService authService;
    private final AppointmentService appointmentService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient")
    public ResponseEntity<List<AppointmentShortViewDto>> getPatientAppointments(
            @AuthenticationPrincipal UUID userId,
            @RequestParam LocalDate start) {
        try {
            List<AppointmentShortViewDto> appointments;
            appointments = appointmentService.getAppointmentsForPatient(userId, start);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentShortViewDto>> getDoctorAppointments(
            @PathVariable UUID doctorId,
            @RequestParam LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        try {
            List<AppointmentShortViewDto> appointments;
            if (end == null)
                appointments = appointmentService.getAppointmentsForDoctor(doctorId, start);
            else
                appointments = appointmentService.getAppointmentsForDoctor(doctorId, start, end);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/doctor/{doctorId}/slots")
    public ResponseEntity<List<AppointmentDto>> getDoctorPublicAppointments(
            @PathVariable("doctorId") UUID doctor,
            @RequestParam LocalDate date) {
        try {
            List<AppointmentDto> appointments = appointmentService.getPublicAppointmentsForDoctor(doctor, date);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/finish")
    @PreAuthorize("hasAnyRole('DOCTOR','LAB_ASSISTANT')")
    public ResponseEntity<?> finishAppointment(
            @RequestPart(value = "medicalFiles", required = false) List<MultipartFile> files,
            @RequestParam("appointmentId") UUID appointmentId,
            @RequestParam("diagnosis") String diagnosis) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UUID userId;
            if (authentication != null && authentication.getPrincipal() instanceof UUID)
                userId = (UUID) authentication.getPrincipal();
             else
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "User not authenticated or principal type is incorrect"));

            List<MedicalFileUploadDto> filesDto = convertMultipartFilesToDto(files);
            appointmentService.finishAppointment(userId, appointmentId, diagnosis, filesDto);
            return ResponseEntity.ok().build();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to store files"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unexpected server error."));
        }
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    public ResponseEntity<?> cancelAppointment(
            @AuthenticationPrincipal UUID userId,
            @RequestParam UUID doctorId,
            @RequestParam UUID patientId,
            @RequestParam UUID appointmentId) throws AccessDeniedException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserRole role = UserRole
                .fromString(auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));

        boolean isRoleMatching;
        switch (role) {
            case PATIENT -> isRoleMatching = userId.equals(patientId);
            case DOCTOR, LAB_ASSISTANT -> isRoleMatching = userId.equals(doctorId);
            default -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        if (!isRoleMatching)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        appointmentService.cancelAppointment(doctorId, patientId, appointmentId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('LAB_ASSISTANT')")
    @PostMapping("/assign/assistant")
    public ResponseEntity<?> assignLabAssistantToAppointment(
            @AuthenticationPrincipal UUID userId,
            @RequestParam UUID appointmentId) throws AccessDeniedException {
        appointmentService.assignLabAssistantToAppointment(userId, appointmentId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR', 'LAB_ASSISTANT')")
    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentFullViewDto> getAppointment(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID appointmentId) throws AccessDeniedException {
        AppointmentFullViewDto dto = appointmentService.getFullAppointment(appointmentId, userId);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping
    public ResponseEntity<?> createAppointment(
            @AuthenticationPrincipal UUID userId,
            @RequestBody AppointmentCreateUpdateDto app) throws AccessDeniedException {
        app.setPatientId(userId);
        appointmentService.createAppointment(app, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler(value = AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAppointmentNotFound(AppointmentNotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
