package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.service.HospitalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/")
    public ResponseEntity<String> createHospital(@RequestBody HospitalDto hospitalDto) {
        try {
            hospitalService.createHospital(hospitalDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
