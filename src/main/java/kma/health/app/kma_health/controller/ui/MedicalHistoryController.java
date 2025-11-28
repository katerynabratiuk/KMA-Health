package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.AppointmentFullViewDto;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.PatientHistoryAccessException;
import kma.health.app.kma_health.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/ui/history")
public class MedicalHistoryController {

    private final PatientService patientService;

    @GetMapping("/{patientId}")
    public String getMedicalHistory(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID patientId,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("ANONYMOUS");

        UserRole userRoleEnum = UserRole.valueOf(userRole);
        UUID accessCheckerId = (userRoleEnum == UserRole.DOCTOR) ? userId : patientId;

        try {
            List<AppointmentFullViewDto> history = patientService.getPatientMedicalHistory(
                    patientId,
                    accessCheckerId,
                    userRoleEnum
            );

            model.addAttribute("closedAppointments", history);
            model.addAttribute("patientId", patientId);

            return "medical_history";

        } catch (PatientHistoryAccessException e) {
            model.addAttribute("errorTitle", "Відмовлено у доступі");
            model.addAttribute("errorMessage", e.getMessage());
            return "error/access_denied";
        }
    }
}