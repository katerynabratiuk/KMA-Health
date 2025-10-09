package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.service.HospitalService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/hospital")
public class HospitalController {

    HospitalService hospitalService;

    @GetMapping()
    public List<HospitalDto> searchHospitals(
            @RequestParam(required = false) String name,
//            @RequestParam(required = false) Double lat,
//            @RequestParam(required = false) Double lon, // TODO
            @RequestParam() Integer pageSize,
            @RequestParam() Integer pageNum
    ){
        return hospitalService.searchHospitals(name, pageNum, pageSize);
    }

}
