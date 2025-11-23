package kma.health.app.kma_health.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalFileUploadDto {
    private UUID id;
    private String fileType;
    private String name;
    private String extension;
    private MultipartFile file;
}
