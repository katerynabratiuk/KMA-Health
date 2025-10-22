package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ExaminationDto {
    @NotNull
    private String name;
    @NotNull
    private String unit;
}
