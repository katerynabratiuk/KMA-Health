package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.AppointmentCreateUpdateDto;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import kma.health.app.kma_health.exception.ErrorResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@AllArgsConstructor
public class AppointmentController {

    private final AuthService authService;
    private final AppointmentService appointmentService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient")
    public ResponseEntity<List<AppointmentShortViewDto>> getPatientAppointments(@RequestHeader("Authorization") String authHeader,
                                                                                @RequestParam LocalDate start, @RequestParam LocalDate end) {
        try {
            String token = authService.extractToken(authHeader);
            UUID patientId = authService.getUserFromToken(token).getId();
            return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(patientId, start, end));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient")
    public ResponseEntity<List<AppointmentShortViewDto>> getPatientAppointments(@RequestHeader("Authorization") String authHeader,
                                                                                @RequestParam LocalDate date) {
        try {
            String token = authService.extractToken(authHeader);
            UUID patientId = authService.getUserFromToken(token).getId();
            return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(patientId, date));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/doctor")
    public ResponseEntity<List<AppointmentShortViewDto>> getDoctorAppointments(@RequestParam UUID doctorId, @RequestParam LocalDate start,
                                                                               @RequestParam LocalDate end) {
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentsForDoctor(doctorId, start, end));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/doctor")
    public ResponseEntity<List<AppointmentShortViewDto>> getDoctorAppointments(@RequestParam UUID doctorId, @RequestParam LocalDate date) {
        try {
            return ResponseEntity.ok(appointmentService.getAppointmentsForDoctor(doctorId, date));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
