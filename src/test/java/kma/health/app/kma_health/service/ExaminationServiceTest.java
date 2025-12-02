package kma.health.app.kma_health.service;

import jakarta.persistence.EntityNotFoundException;
import kma.health.app.kma_health.dto.ExaminationDto;
import kma.health.app.kma_health.entity.Examination;
import kma.health.app.kma_health.exception.ExaminationNotFoundException;
import kma.health.app.kma_health.repository.ExaminationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExaminationServiceTest {

    @Mock
    private ExaminationRepository examinationRepository;

    @InjectMocks
    private ExaminationService examinationService;

    @Test
    public void testFindExaminationById_ShouldReturnExamination() {
        long examinationId = 1L;
        Examination examination = new Examination();
        examination.setId(examinationId);
        examination.setExamName("Blood Test");

        when(examinationRepository.findById(examinationId)).thenReturn(Optional.of(examination));

        Examination result = examinationService.findExaminationById(examinationId);

        assertNotNull(result);
        assertEquals("Blood Test", result.getExamName());
    }

    @Test
    public void testFindExaminationById_ShouldThrowExceptionWhenNotFound() {
        long examinationId = 1L;

        when(examinationRepository.findById(examinationId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            examinationService.findExaminationById(examinationId);
        });
    }

    @Test
    public void testCreateExamination_ShouldSaveNewExamination() {
        ExaminationDto dto = new ExaminationDto("Blood Test", "ml");

        when(examinationRepository.findByExamNameAndUnit("Blood Test", "ml")).thenReturn(Optional.empty());

        examinationService.createExamination(dto);

        verify(examinationRepository, times(1)).save(any(Examination.class));
    }

    @Test
    public void testCreateExamination_ShouldThrowExceptionWhenExists() {
        ExaminationDto dto = new ExaminationDto("Blood Test", "ml");

        Examination existingExam = new Examination();
        existingExam.setExamName("Blood Test");
        existingExam.setUnit("ml");

        when(examinationRepository.findByExamNameAndUnit("Blood Test", "ml")).thenReturn(Optional.of(existingExam));

        assertThrows(DataIntegrityViolationException.class, () -> {
            examinationService.createExamination(dto);
        });

        verify(examinationRepository, never()).save(any(Examination.class));
    }

    @Test
    public void testDeleteExamination_ShouldDeleteExistingExamination() {
        ExaminationDto dto = new ExaminationDto("Blood Test", "ml");

        Examination existingExam = new Examination();
        existingExam.setExamName("Blood Test");
        existingExam.setUnit("ml");

        when(examinationRepository.findByExamNameAndUnit("Blood Test", "ml")).thenReturn(Optional.of(existingExam));

        examinationService.deleteExamination(dto);

        verify(examinationRepository, times(1)).delete(existingExam);
    }

    @Test
    public void testDeleteExamination_ShouldThrowExceptionWhenNotFound() {
        ExaminationDto dto = new ExaminationDto("NonExistent", "ml");

        when(examinationRepository.findByExamNameAndUnit("NonExistent", "ml")).thenReturn(Optional.empty());

        assertThrows(ExaminationNotFoundException.class, () -> {
            examinationService.deleteExamination(dto);
        });

        verify(examinationRepository, never()).delete(any(Examination.class));
    }
}

