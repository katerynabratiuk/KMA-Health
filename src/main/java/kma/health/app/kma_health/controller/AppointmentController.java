package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import kma.health.app.kma_health.exception.ErrorResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@AllArgsConstructor
public class AppointmentController {

    private final AuthService authService;
    private final AppointmentService appointmentService;

    @GetMapping()
    public List<AppointmentShortViewDto> getPatientAppointments(@RequestHeader("Authorization") String authHeader) {
        String token = authService.extractToken(authHeader);
        String patientId = authService.getUserFromToken(token).getPassportNumber();
        return appointmentService.getAppointments(patientId);
    }

    @GetMapping("/{appointmentId}")
    public AppointmentFullViewDto getAppointment(@PathVariable UUID appointmentId) {
        return appointmentService.getFullAppointment(appointmentId);
    }

    @ExceptionHandler(value = AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAppointmentNotFound(AppointmentNotFoundException e) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

}
