package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.EditHospitalRequest;
import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.dto.HospitalFormDto;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.entity.Examination;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.exception.CoordinatesNotFoundException;
import kma.health.app.kma_health.repository.HospitalRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HospitalService {

    public static final LocalTime EXAMINATION_TIME = LocalTime.of(8, 0);
    private final HospitalRepository hospitalRepository;
    private final HospitalGeocodingService hospitalGeocodingService;

    public void createHospital(HospitalDto hospital) {
        Hospital newHospital = new Hospital();
        newHospital.setName(hospital.getName());
        newHospital.setAddress(hospital.getAddress());
        newHospital.setCity(hospital.getCity());
        HospitalGeocodingService.Coordinates coordinates;
        try {
            coordinates =
                    hospitalGeocodingService.getCoordinatesByAddress(hospital.getAddress());
        } catch (CoordinatesNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        newHospital.setLatitude(coordinates.getLatitude());
        newHospital.setLongitude(coordinates.getLongitude());
        newHospital.setType(hospital.getType());
        hospitalRepository.save(newHospital);
    }

    public void editHospitalAddress(EditHospitalRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getId()).orElse(null);
        if (hospital == null) throw new IllegalArgumentException("Hospital not found");
        hospital.setAddress(request.getAddress());
        HospitalGeocodingService.Coordinates coordinates;
        try {
            coordinates =
                    hospitalGeocodingService.getCoordinatesByAddress(request.getAddress());
        } catch (CoordinatesNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        hospital.setLatitude(coordinates.getLatitude());
        hospital.setLongitude(coordinates.getLongitude());
        hospital.setCity(request.getCity());
        hospitalRepository.save(hospital);
    }

    public void deleteHospital(Long id) {
        Hospital hospital = hospitalRepository.findById(id).orElse(null);
        if (hospital == null) throw new IllegalArgumentException("Hospital not found");
        hospitalRepository.delete(hospital);
    }

    public List<HospitalDto> searchHospitals(String name, Integer pageSize, Integer pageNum) {
        int page = (pageNum != null && pageNum >= 0) ? pageNum : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 20;

        Specification<Hospital> spec = (root, q, cb) -> cb.conjunction();

        if (name != null && !name.isBlank()) {
            String like = "%" + name.trim().toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("name")), like));
        }

        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        return hospitalRepository.findAll(spec, pageable)
                .getContent()
                .stream()
                .map(HospitalDto::fromEntity)
                .toList();
    }

    public HospitalDto getHospital(Long id) {
        var h = hospitalRepository.getReferenceById(id);
        return HospitalDto.fromEntity(h);
    }

    public List<HospitalFormDto> getAllHospitals() {
        return hospitalRepository.findAll()
                .stream()
                .map(HospitalFormDto::fromEntity)
                .toList();
    }

    public boolean providesExamination(Hospital hospital, Examination examination) {
        return hospital != null
               && examination != null
               && hospital.getExaminations() != null
               && hospital.getExaminations().contains(examination);
    }

    public boolean providesDoctorType(Hospital hospital, DoctorType doctorType) {
        return hospital != null
               && doctorType != null
               && doctorType.getDoctors() != null
               && doctorType.getDoctors()
                       .stream()
                       .anyMatch(d -> hospital.equals(d.getHospital()));
    }

    public List<String> getAllCities() {
        return hospitalRepository.findAll()
                .stream()
                .map(Hospital::getCity)
                .distinct()
                .collect(Collectors.toList());
    }
}
