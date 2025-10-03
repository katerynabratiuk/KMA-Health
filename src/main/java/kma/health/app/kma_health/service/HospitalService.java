package kma.health.app.kma_health.service;

import kma.health.app.kma_health.dto.EditHospitalRequest;
import kma.health.app.kma_health.dto.HospitalDto;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.exceptions.CoordinatesNotFoundException;
import kma.health.app.kma_health.repository.HospitalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HospitalService {

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
}
