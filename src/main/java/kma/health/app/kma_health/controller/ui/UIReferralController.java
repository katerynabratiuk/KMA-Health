package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.ReferralService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
@RequestMapping("/ui")
@AllArgsConstructor
public class UIReferralController {

    private final ReferralService referralService;
    private final AppointmentService appointmentService;

    @GetMapping("/referrals/{patientId}")
    public String getReferralsPage(@PathVariable UUID patientId, Model model) throws AccessDeniedException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID currentUserId = (UUID) authentication.getPrincipal();

        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("ANONYMOUS");

        boolean hasAccess = false;

        if (userRole.equals("PATIENT")) {
            if (currentUserId.equals(patientId))
                hasAccess = true;
        } else if (userRole.equals("DOCTOR")) {
            if (appointmentService.haveOpenAppointment(currentUserId, patientId))
                hasAccess = true;
        }

        if (!hasAccess)
            throw new AccessDeniedException("Access denied to referrals for patient ID: " + patientId);

        List<ReferralDto> referrals = referralService.getActiveReferrals(patientId);

        model.addAttribute("referrals", referrals);
        model.addAttribute("userRole", userRole);
        model.addAttribute("patientId", patientId);

        return "referrals";
    }
}
