package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.MedicalFile;
import kma.health.app.kma_health.entity.Patient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MedicalFileDtoTest {

    @Test
    void testMedicalFileDto_FromEntity() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        UUID fileId = UUID.randomUUID();
        MedicalFile medicalFile = new MedicalFile();
        medicalFile.setId(fileId);
        medicalFile.setName("Test File");
        medicalFile.setLink("/files/test.pdf");
        medicalFile.setExtension("pdf");
        medicalFile.setFileType("application/pdf");
        medicalFile.setPatient(patient);

        MedicalFileDto dto = new MedicalFileDto(medicalFile);

        assertEquals(fileId, dto.getId());
        assertEquals("Test File", dto.getName());
        assertEquals("/files/test.pdf", dto.getLink());
        assertEquals("pdf", dto.getExtension());
        assertEquals("application/pdf", dto.getFileType());
    }

    @Test
    void testMedicalFileDto_SettersAndGetters() {
        MedicalFileDto dto = new MedicalFileDto();

        UUID id = UUID.randomUUID();
        dto.setId(id);
        dto.setName("Test File");
        dto.setLink("/files/test.pdf");
        dto.setExtension("pdf");
        dto.setFileType("application/pdf");

        assertEquals(id, dto.getId());
        assertEquals("Test File", dto.getName());
        assertEquals("/files/test.pdf", dto.getLink());
        assertEquals("pdf", dto.getExtension());
        assertEquals("application/pdf", dto.getFileType());
    }
}

