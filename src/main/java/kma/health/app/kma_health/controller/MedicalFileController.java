package kma.health.app.kma_health.controller;

import kma.health.app.kma_health.service.MedicalTestsTemplateGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class MedicalFileController {

    private final MedicalTestsTemplateGeneratorService testGenerator;

    @GetMapping("/template")
    public ResponseEntity<Resource> downloadBoilerplate(
            @RequestParam List<Long> testIds,
            @RequestParam String patientName
    ) {
        String filePath = testGenerator.generateTemplate(testIds, patientName);
        File file = new File(filePath);

        if (!file.exists())
            return ResponseEntity.internalServerError().build();

        Resource resource = new FileSystemResource(file);
        String contentDisposition = "attachment; filename=\"" + file.getName() + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(file.length())
                .body(resource);
    }
}

