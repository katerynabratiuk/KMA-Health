package kma.health.app.kma_health.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DoctorSearchDto {
    private String doctorType;
    private String city;
    private Long hospitalId;
    private String query;

    @NotNull(message = "sortBy cannot be null")
    private SortBy sortBy = new SortBy("rating", "asc");

    @Data
    public static class SortBy {
        @Pattern(regexp = "rating|distance", message = "param must be 'rating' or 'distance'")
        private String param;

        @Pattern(regexp = "asc|dsc", message = "direction must be 'asc' or 'dsc'")
        private String direction;

        public SortBy() {
            this.param = "rating";
            this.direction = "asc";
        }

        public SortBy(String param, String direction) {
            this.param = param;
            this.direction = direction;
        }
    }
}

