package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.dto.DoctorTypeDto;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.service.DoctorTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/doctortype")
public class DoctorTypeController {

    private final DoctorTypeService doctorTypeService;

    @PostMapping("/")
    public ResponseEntity<DoctorType> createDoctorType(@RequestBody DoctorTypeDto doctorTypeDto) {
        try {
            doctorTypeService.createDoctorType(doctorTypeDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
