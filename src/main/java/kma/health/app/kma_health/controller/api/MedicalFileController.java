package kma.health.app.kma_health.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import kma.health.app.kma_health.repository.MedicalFileRepository;
import kma.health.app.kma_health.service.MedicalTestsTemplateGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/files")
public class MedicalFileController {

    private final MedicalTestsTemplateGeneratorService testGenerator;
    private final MedicalFileRepository medicalFileRepository;

    @Value("${root.file.path}")
    private String rootPath;

    @PreAuthorize("hasRole('LAB_ASSISTANT')")
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

    @GetMapping
    public ResponseEntity<Resource> getFile(@RequestParam String downloadLink) throws IOException {
        Path filePath = Paths.get(rootPath).resolve(downloadLink.substring(1)).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + Paths.get(downloadLink).getFileName() + "\"")
                .body(resource);
    }
}


