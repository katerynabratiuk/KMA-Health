package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.NotNull;
import kma.health.app.kma_health.enums.HospitalType;
import lombok.Data;

@Data
public class HospitalSearchDto {
    private String request;
    private String city;
    private HospitalType hospitalType;

    @NotNull(message = "sortBy cannot be null")
    private DoctorSearchDto.SortBy sortBy = new DoctorSearchDto.SortBy("rating", "asc");
}
