package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.AppointmentCreateUpdateDto;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.dto.MedicalFileUploadDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import kma.health.app.kma_health.exception.ErrorResponse;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@AllArgsConstructor
public class AppointmentController {

    private final AuthService authService;
    private final AppointmentService appointmentService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient")
    public ResponseEntity<List<AppointmentShortViewDto>> getPatientAppointments(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam LocalDate start,
            @RequestParam(required = false) LocalDate end
    ) {
        try {
            String token = authService.extractToken(authHeader);
            UUID patientId = authService.getUserFromToken(token).getId();
            List<AppointmentShortViewDto> appointments;
            if (end == null)
                appointments = appointmentService.getAppointmentsForPatient(patientId, start);
            else
                appointments = appointmentService.getAppointmentsForPatient(patientId, start, end);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/doctor")
    public ResponseEntity<List<AppointmentShortViewDto>> getDoctorAppointments(
            @RequestParam UUID doctorId,
            @RequestParam LocalDate start,
            @RequestParam(required = false) LocalDate end
    ) {
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

    @PostMapping("/finish")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> finishAppointment(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("files") List<MedicalFileUploadDto> filesDto,
            @RequestParam UUID appointmentId,
            @RequestParam String diagnosis
    ) {
        Doctor doctor = (Doctor) authService.getUserFromToken(authHeader);
        try {
            appointmentService.finishAppointment(doctor.getId(), appointmentId, diagnosis, filesDto);
            return ResponseEntity.ok().build();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to store files"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Unexpected error occurred"));
        }
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('DOCTOR, PATIENT')")
    public ResponseEntity<?> cancelAppointment(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam UUID doctorId,
            @RequestParam UUID patientId,
            @RequestParam UUID appointmentId
    ) throws AccessDeniedException {
        UserRole role = authService.getUserFromToken(authHeader).getRole();
        boolean isRoleMatching;
        switch (role) {
            case PATIENT -> {
                isRoleMatching = authService.getUserFromToken(authHeader).getId() == patientId;
            }
            case DOCTOR -> {
                isRoleMatching = authService.getUserFromToken(authHeader).getId() == doctorId;
            }
            default -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        if (!isRoleMatching)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        appointmentService.cancelAppointment(doctorId, patientId, appointmentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{appointmentId}")
    public AppointmentFullViewDto getAppointment(@PathVariable UUID appointmentId) {
        return appointmentService.getFullAppointment(appointmentId);
    }

    @PreAuthorize("hasAnyRole('PATIENT','DOCTOR')")
    @PostMapping
    public void createAppointment(@RequestBody AppointmentCreateUpdateDto app) {
        appointmentService.createAppointment(app);
    }

    @ExceptionHandler(value = AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAppointmentNotFound(AppointmentNotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

}
