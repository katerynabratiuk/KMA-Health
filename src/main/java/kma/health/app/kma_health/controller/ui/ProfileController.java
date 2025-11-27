package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.ProfileDto;
import kma.health.app.kma_health.service.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/ui/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public String getProfilePage(
            @AuthenticationPrincipal UUID userId,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("ANONYMOUS");

        ProfileDto profileDto = profileService.getProfileData(userId, userRole);

        model.addAttribute("user", profileDto);
        model.addAttribute("userRole", userRole);
        model.addAttribute("plannedAppointments", profileDto.getPlannedAppointments());

        return "profile";
    }
}
