package kma.health.app.kma_health.controller.ui;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.exception.AppointmentNotFoundException;
import kma.health.app.kma_health.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/appointments")
public class UIAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/{appointmentId}")
    public String appointment(@PathVariable UUID appointmentId,
            @AuthenticationPrincipal UUID userId,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("ANONYMOUS");

        if (userId == null)
            return "redirect:/ui/login";

        try {
            AppointmentFullViewDto appointmentDto = appointmentService.getFullAppointment(appointmentId, userId);

            model.addAttribute("appointment", appointmentDto);
            model.addAttribute("userRole", userRole);
            model.addAttribute("userId", userId);

            return "appointment";
        } catch (AccessDeniedException e) {
            return "error/403";
        } catch (EntityNotFoundException | AppointmentNotFoundException e) {
            return "error/404";
        }
    }

    @GetMapping()
    public String getPatientAppointments(@AuthenticationPrincipal UUID userId,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("ANONYMOUS");

        List<AppointmentFullViewDto> appointments = appointmentService.getAppointmentsForPatient(userId);

        model.addAttribute("appointments", appointments);
        model.addAttribute("userRole", userRole);
        model.addAttribute("userId", userId);

        return "appointments";
    }
}