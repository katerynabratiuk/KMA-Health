package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.doctorDetail.DoctorDetailDto;
import kma.health.app.kma_health.service.DoctorSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/public/doctors")
public class DoctorController {

    private final DoctorSearchService doctorService;


    @GetMapping("/{id}")
    public String doctorDetail(@PathVariable("id") UUID doctorId, Model model) {
        DoctorDetailDto doctor = doctorService.getDoctorById(doctorId);

        if (doctor == null) {
            return "redirect:/ui/public/";
        }
        model.addAttribute("doctor", doctor);
        return "doctor-detail";
    }
}
