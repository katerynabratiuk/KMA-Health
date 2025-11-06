package kma.health.app.kma_health.controller.ui;

import jakarta.validation.Valid;
import kma.health.app.kma_health.dto.RegisterRequest;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.service.DoctorTypeService;
import kma.health.app.kma_health.service.HospitalService;
import kma.health.app.kma_health.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/public")
public class RegisterController {

    private final RegistrationService registrationService;
    private final HospitalService hospitalService;
    private final DoctorTypeService doctorTypeService;

    @ModelAttribute("hospitals")
    public Object hospitals() {
        return hospitalService.getAllHospitals();
    }

    @ModelAttribute("doctorTypes")
    public Object doctorTypes() {
        return doctorTypeService.getAllDoctorTypes();
    }

    @GetMapping("/register")
    public String register() {
        return "register-options";
    }

    @GetMapping("/register/doctor")
    public String doctorRegister(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register-doctor";
    }

    @PostMapping("/register/doctor")
    public String doctorRegisterPost(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes ra,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "register-doctor";
        }
        try {
            registerRequest.setRole(UserRole.DOCTOR);
            registrationService.register(registerRequest);
        } catch (RuntimeException e) {
            model.addAttribute("globalError", e.getMessage());
            return "register-doctor";
        }
        ra.addFlashAttribute("success", "Лікаря створено. Увійдіть, будь ласка.");
        return "redirect:/ui/public/"; // або "redirect:/ui/public/home"
    }

    @GetMapping("/register/patient")
    public String patientRegister(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "register-patient";
    }

    @PostMapping("/register/patient")
    public String patientRegisterPost(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes ra,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "register-patient";
        }
        try {
            registerRequest.setRole(UserRole.PATIENT);
            registrationService.register(registerRequest);
        } catch (RuntimeException e) {
            model.addAttribute("globalError", e.getMessage());
            return "register-patient";
        }
        return "redirect:/ui/public/";
    }
}
