package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.DoctorTypeDto;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.repository.DoctorTypeRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorTypeService {

    private final DoctorTypeRepository doctorTypeRepository;

    @Getter
    @Value("${family_doctor.type_name}")
    private String familyDoctorTypeName;

    public void createDoctorType(DoctorTypeDto dto) {
        Optional<DoctorType> doctorType = doctorTypeRepository.findByTypeName(dto.getTypeName());
        if (doctorType.isPresent())
            throw new DataIntegrityViolationException("Doctor type " + dto.getTypeName() + " already exists");
        DoctorType doctorTypeEntity = new DoctorType();
        doctorTypeEntity.setTypeName(dto.getTypeName());
        doctorTypeRepository.save(doctorTypeEntity);
    }

    public void deleteDoctorType(DoctorTypeDto dto) {
        Optional<DoctorType> doctorType = doctorTypeRepository.findByTypeName(dto.getTypeName());
        if (doctorType.isEmpty())
            throw new EntityNotFoundException("Doctor type " + dto.getTypeName() + " does not exist");
        doctorTypeRepository.delete(doctorType.get());
    }

    @Cacheable("doctorTypes")
    public List<DoctorType> getAllDoctorTypes() {
        return doctorTypeRepository.findAll();
    }

    @Cacheable("doctorTypesNames")
    public List<String> getAllDoctorTypeNames() {
        return doctorTypeRepository.findAll()
                .stream()
                .map(DoctorType::getTypeName)
                .collect(Collectors.toList());
    }
}
