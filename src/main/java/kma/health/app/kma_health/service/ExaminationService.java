package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.ExaminationDto;
import kma.health.app.kma_health.entity.Examination;
import kma.health.app.kma_health.exception.ExaminationNotFoundException;
import kma.health.app.kma_health.repository.ExaminationRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ExaminationService {
    private final ExaminationRepository examinationRepository;

    public Examination findExaminationById(long examinationId) {
        return examinationRepository.findById(examinationId)
                .orElseThrow(() -> new EntityNotFoundException("Examination " + examinationId + " not found"));
    }

    public void createExamination(ExaminationDto dto) {
        Optional<Examination> examOpt = examinationRepository.findByExamNameAndUnit(dto.getName(), dto.getUnit());
        if (examOpt.isPresent())
            throw new DataIntegrityViolationException("Examination with name " + dto.getName() + " and unit " + dto.getUnit() + " already exists");
        Examination exam = new Examination();
        exam.setExamName(dto.getName());
        exam.setUnit(dto.getUnit());
        examinationRepository.save(exam);
    }

    public void deleteExamination(ExaminationDto dto) {
        Optional<Examination> examOpt = examinationRepository.findByExamNameAndUnit(dto.getName(), dto.getUnit());
        if (examOpt.isEmpty())
            throw new ExaminationNotFoundException("Examination with name " + dto.getName() + " and unit " + dto.getUnit() + " does not exist");
        examinationRepository.delete(examOpt.get());
    }
}
