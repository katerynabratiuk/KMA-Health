package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.dto.HospitalSearchDto;
import kma.health.app.kma_health.service.DoctorSearchService;
import kma.health.app.kma_health.service.HospitalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui/public")
public class HomeController {
    private final DoctorSearchService doctorSearchService;
    private final HospitalSearchService hospitalSearchService;

    @GetMapping({"/", ""})
    public String home(Model model) {
        var dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));
        model.addAttribute("dto", dto);
        model.addAttribute("doctors", null);
        model.addAttribute("searchPerformed", false);
        return "home";
    }

    @PostMapping("/search")
    public String processSearch(
            @RequestParam(defaultValue = "doctor") String searchType,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String doctorType,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "rating-asc") String sort,
            @RequestParam(defaultValue = "0") double userLat,
            @RequestParam(defaultValue = "0") double userLon,
            Model model
    ) {
        String param = "";
        String direction = "";
        if (sort != null && sort.contains("-")) {
            String[] parts = sort.split("-");
            param = parts[0];
            direction = parts.length > 1 ? parts[1] : "asc";
        }

        var doctorDto = new DoctorSearchDto();
        doctorDto.setQuery(query != null ? query : "");
        doctorDto.setDoctorType(doctorType != null && !doctorType.isEmpty() ? doctorType : null);
        doctorDto.setCity(city != null && !city.isEmpty() ? city : null);
        doctorDto.setSortBy(new DoctorSearchDto.SortBy(param, direction));

        var hospitalDto = new HospitalSearchDto();
        hospitalDto.setCity(city != null && !city.isEmpty() ? city : null);
        hospitalDto.setRequest(query != null ? query : "");
        hospitalDto.setSortBy(new DoctorSearchDto.SortBy(param, direction));

        if ("clinic".equalsIgnoreCase(searchType)) {
            try {
                var hospitals = hospitalSearchService.searchHospitals(hospitalDto, userLat, userLon);
                model.addAttribute("hospitals", hospitals);
                model.addAttribute("doctors", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                var doctors = doctorSearchService.searchDoctors(doctorDto, userLat, userLon);
                model.addAttribute("doctors", doctors);
                model.addAttribute("hospitals", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("searchType", searchType);
        model.addAttribute("doctorDto", doctorDto);
        model.addAttribute("hospitalDto", hospitalDto);
        model.addAttribute("searchPerformed", true);

        return "home";
    }
}
