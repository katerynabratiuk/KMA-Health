package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.dto.HospitalSearchDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.service.DoctorSearchService;
import kma.health.app.kma_health.service.HospitalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/public")
public class HomeController
{
    private final DoctorSearchService doctorSearchService;
    private final HospitalSearchService hospitalSearchService;

    @GetMapping("/")
    public String home(Model model)
    {
        var dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));
        model.addAttribute("dto", dto);
        model.addAttribute("doctors", null);
        return "home";
    }

    @PostMapping("/search")
    public String processSearch(
            @RequestParam(defaultValue = "doctor") String searchType,
            @ModelAttribute DoctorSearchDto doctorDto,
            @ModelAttribute HospitalSearchDto hospitalDto,
            @RequestParam(defaultValue = "0") double userLat,
            @RequestParam(defaultValue = "0") double userLon,
            Model model
    ) {
        if ("clinic".equalsIgnoreCase(searchType)) {
            var hospitals = hospitalSearchService.searchHospitals(hospitalDto, userLat, userLon);
            model.addAttribute("hospitals", hospitals);
            model.addAttribute("doctors", null);
        } else {
            var doctors = doctorSearchService.searchDoctors(doctorDto, userLat, userLon);
            model.addAttribute("doctors", doctors);
            model.addAttribute("hospitals", null);
        }

        model.addAttribute("searchType", searchType);
        model.addAttribute("doctorDto", doctorDto);
        model.addAttribute("hospitalDto", hospitalDto);

        if (doctorDto.getSortBy() == null)
            doctorDto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        return "home";
    }
}
