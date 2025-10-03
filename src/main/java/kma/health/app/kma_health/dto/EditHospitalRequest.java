package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditHospitalRequest {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotNull
    private String city;
}
