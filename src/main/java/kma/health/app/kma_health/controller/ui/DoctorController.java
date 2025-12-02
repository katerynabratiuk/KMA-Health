package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.doctorDetail.DoctorDetailDto;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.DoctorSearchService;
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

import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/public/doctors")
public class DoctorController {

    private final DoctorSearchService doctorService;

    @GetMapping("/{id}")
    public String doctorDetail(@PathVariable("id") UUID doctorId,
            @AuthenticationPrincipal UUID userId,
            Model model) {
        // Get user role if authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = null;
        if (authentication != null && authentication.isAuthenticated()
                && !("anonymousUser".equals(authentication.getPrincipal()))) {
            userRole = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.substring(5))
                    .findFirst()
                    .orElse(null);
        }

        DoctorDetailDto doctor = doctorService.getDoctorDetailById(doctorId, Optional.ofNullable(userId));
        if (doctor == null) {
            return "redirect:/ui/public/";
        }
        model.addAttribute("doctor", doctor);
        model.addAttribute("userRole", userRole);
        return "doctor-detail";
    }
}
