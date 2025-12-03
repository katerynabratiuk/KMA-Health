package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.AppointmentShortViewDto;
import kma.health.app.kma_health.dto.ProfileDto;
import kma.health.app.kma_health.service.AppointmentService;
import kma.health.app.kma_health.service.ProfileService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/ui/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final AppointmentService appointmentService;

    public ProfileController(ProfileService profileService, AppointmentService appointmentService) {
        this.profileService = profileService;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/view")
    public String getProfilePage(
            @AuthenticationPrincipal UUID userId,
            @RequestParam UUID profileId,
            @RequestParam String profileRole,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("ANONYMOUS");

        ProfileDto profileDto = null;

        if (userRole.equals("PATIENT") && userId.equals(profileId) ||
            userRole.equals("DOCTOR")
            && profileRole.equals("PATIENT")
            && appointmentService.haveOpenAppointment(userId, profileId)) {
            profileDto = profileService.getProfileData(profileId, profileRole);
        }

        if (profileDto == null)
            return null;

        model.addAttribute("user", profileDto);
        model.addAttribute("userRole", profileRole);
        model.addAttribute("plannedAppointments", profileDto.getPlannedAppointments());

        return "profile";
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

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/calendar")
    public String getCalendar(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {

        YearMonth currentYearMonth = (year != null && month != null)
                ? YearMonth.of(year, month)
                : YearMonth.now();

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();

        List<AppointmentShortViewDto> appointments = appointmentService.getAppointmentsForDoctor(
                userId,
                firstDayOfMonth,
                lastDayOfMonth);

        model.addAttribute("appointments", appointments);
        model.addAttribute("currentYear", currentYearMonth.getYear());
        model.addAttribute("currentMonth", currentYearMonth.getMonthValue());
        model.addAttribute("monthName", currentYearMonth.getMonth().toString());
        model.addAttribute("firstDayOfMonth", firstDayOfMonth);
        model.addAttribute("daysInMonth", currentYearMonth.lengthOfMonth());
        model.addAttribute("firstDayOfWeek", firstDayOfMonth.getDayOfWeek().getValue());

        return "doctor-calendar";
    }
}
