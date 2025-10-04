package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.entity.MedicalFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MedicalFileDto {
    private UUID id;
    private String fileType;
    private String name;
    private String extension;
    private String link;

    public MedicalFileDto(MedicalFile medicalFile)
    {
        this.id = medicalFile.getId();
        this.fileType = medicalFile.getFileType();
        this.name = medicalFile.getName();
        this.extension = medicalFile.getExtension();
        this.link = medicalFile.getLink();
    }
}
