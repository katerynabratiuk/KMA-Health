package kma.health.app.kma_health.controller.ui;

import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.dto.HospitalSearchDto;
import kma.health.app.kma_health.dto.SearchFormDto;
import kma.health.app.kma_health.service.DoctorSearchService;
import kma.health.app.kma_health.service.DoctorTypeService;
import kma.health.app.kma_health.service.HospitalSearchService;
import kma.health.app.kma_health.service.HospitalService;
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
    private final HospitalService hospitalService;
    private final DoctorTypeService doctorTypeService;

    @GetMapping({"/"})
    public String home(Model model) {
        SearchFormDto formDto = new SearchFormDto();
        formDto.setSearchType("doctor");
        formDto.setSort("rating-asc");
        formDto.setUserLat(0);
        formDto.setUserLon(0);

        model.addAttribute("formDto", formDto);
        model.addAttribute("doctors", null);
        model.addAttribute("hospitals", null);
        model.addAttribute("searchPerformed", false);

        model.addAttribute("cities", hospitalService.getAllCities());
        model.addAttribute("specialties", doctorTypeService.getAllDoctorTypeNames());

        return "home";
    }

    @PostMapping("/search")
    public String processSearch(@ModelAttribute SearchFormDto formDto, Model model) {
        String param = "";
        String direction = "";
        if (formDto.getSort() != null && formDto.getSort().contains("-")) {
            String[] parts = formDto.getSort().split("-");
            param = parts[0];
            direction = parts.length > 1 ? parts[1] : "asc";
        }

        var doctorDto = new DoctorSearchDto();
        doctorDto.setQuery(formDto.getQuery() != null ? formDto.getQuery() : "");
        doctorDto.setDoctorType(formDto.getDoctorType());
        doctorDto.setCity(formDto.getCity());
        doctorDto.setSortBy(new DoctorSearchDto.SortBy(param, direction));

        var hospitalDto = new HospitalSearchDto();
        hospitalDto.setCity(formDto.getCity());
        hospitalDto.setRequest(formDto.getQuery());
        hospitalDto.setSortBy(new DoctorSearchDto.SortBy(param, direction));

        if ("clinic".equalsIgnoreCase(formDto.getSearchType())) {
            try {
                var hospitals = hospitalSearchService.searchHospitals(hospitalDto, formDto.getUserLat(), formDto.getUserLon());
                model.addAttribute("hospitals", hospitals);
                model.addAttribute("doctors", null);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("hospitals", null);
                model.addAttribute("doctors", null);
                model.addAttribute("searchError", "Failed to search hospitals: " + e.getMessage());
            }
        } else {
            try {
                var doctors = doctorSearchService.searchDoctors(doctorDto, formDto.getUserLat(), formDto.getUserLon());
                model.addAttribute("doctors", doctors);
                model.addAttribute("hospitals", null);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("doctors", null);
                model.addAttribute("hospitals", null);
                model.addAttribute("searchError", "Failed to search doctors: " + e.getMessage());
            }
        }

        model.addAttribute("formDto", formDto);
        model.addAttribute("searchPerformed", true);
        model.addAttribute("cities", hospitalService.getAllCities());
        model.addAttribute("specialties", doctorTypeService.getAllDoctorTypeNames());

        return "home";
    }
}
