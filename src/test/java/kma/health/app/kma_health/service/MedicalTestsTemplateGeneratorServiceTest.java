package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Examination;
import kma.health.app.kma_health.exception.ExaminationNotFoundException;
import kma.health.app.kma_health.repository.ExaminationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalTestsTemplateGeneratorServiceTest {

    @Mock
    private ExaminationRepository examinationRepository;

    private MedicalTestsTemplateGeneratorService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new MedicalTestsTemplateGeneratorService(examinationRepository);
        ReflectionTestUtils.setField(service, "absolutePath", tempDir.toString());
    }

    @Test
    void testGenerateTemplate_Success() {
        Examination exam1 = new Examination();
        exam1.setId(1L);
        exam1.setExamName("Blood Test");
        exam1.setUnit("ml");

        Examination exam2 = new Examination();
        exam2.setId(2L);
        exam2.setExamName("Urine Test");
        exam2.setUnit("ml");

        when(examinationRepository.findById(1L)).thenReturn(Optional.of(exam1));
        when(examinationRepository.findById(2L)).thenReturn(Optional.of(exam2));

        String filePath = service.generateTemplate(List.of(1L, 2L), "John Doe");

        assertNotNull(filePath);
        assertTrue(filePath.contains("John_Doe"));
        assertTrue(filePath.endsWith(".xlsx"));

        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    void testGenerateTemplate_ExaminationNotFound() {
        when(examinationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ExaminationNotFoundException.class, () -> {
            service.generateTemplate(List.of(999L), "John Doe");
        });
    }

    @Test
    void testGenerateTemplate_WithNullUnit() {
        Examination exam = new Examination();
        exam.setId(1L);
        exam.setExamName("Test");
        exam.setUnit(null);

        when(examinationRepository.findById(1L)).thenReturn(Optional.of(exam));

        String filePath = service.generateTemplate(List.of(1L), "Test Patient");

        assertNotNull(filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    void testGenerateTemplate_WithSpecialCharactersInName() {
        Examination exam = new Examination();
        exam.setId(1L);
        exam.setExamName("Test");
        exam.setUnit("ml");

        when(examinationRepository.findById(1L)).thenReturn(Optional.of(exam));

        String filePath = service.generateTemplate(List.of(1L), "John  Doe   Smith");

        assertNotNull(filePath);
        assertTrue(filePath.contains("John_Doe_Smith") || filePath.contains("John__Doe"));
    }

    @Test
    void testGenerateTemplate_EmptyTestIds() {
        String filePath = service.generateTemplate(List.of(), "John Doe");

        assertNotNull(filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
    }
}

