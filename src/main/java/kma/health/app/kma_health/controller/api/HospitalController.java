package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.service.HospitalService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/hospital")
public class HospitalController {

    HospitalService hospitalService;

    @GetMapping()
    public List<HospitalDto> getHospitals(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "pageNum", defaultValue = "0")  int pageNum,
            @RequestParam(name = "pageSize", defaultValue = "20") int pageSize
    ) {
        int page = Math.max(0, pageNum);
        int size = Math.max(1, pageSize);
        return hospitalService.searchHospitals(name, size, page);
    }

    @GetMapping("/{hospitalId}")
    public HospitalDto getHospital(@PathVariable Long hospitalId) {
        return hospitalService.getHospital(hospitalId);
    }

}
