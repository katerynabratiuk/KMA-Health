package kma.health.app.kma_health.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public static List<MedicalFileUploadDto> convertMultipartFilesToDto(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Collections.emptyList();
        }
        return files.stream()
                .map(file -> {
                    String originalFilename = file.getOriginalFilename();
                    int dotIndex = originalFilename != null ? originalFilename.lastIndexOf('.') : -1;
                    String extension = (dotIndex > 0) ? originalFilename.substring(dotIndex + 1) : "";
                    String name = (dotIndex > 0) ? originalFilename.substring(0, dotIndex) : originalFilename;

                    return new MedicalFileUploadDto(
                            null,
                            "Medical File",
                            name,
                            extension,
                            file
                    );
                })
                .collect(Collectors.toList());
    }
}
