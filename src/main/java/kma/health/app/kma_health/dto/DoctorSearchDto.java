package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DoctorSearchDto {

    private String doctorType;
    private String city;
    private Long hospitalId;
    private String doctorName;
    @NotNull(message = "sortBy cannot be null")
    private SortBy sortBy;

    /**
     * @param param     "distance" | "rating"
     * @param direction "asc" | "dsc"
     */
    public record SortBy(
            @Pattern(regexp = "rating|distance", message = "param must be 'rating' or 'distance'")
            String param,

            @Pattern(regexp = "asc|dsc", message = "direction must be 'asc' or 'dsc'")
            String direction
    ) {}
}

