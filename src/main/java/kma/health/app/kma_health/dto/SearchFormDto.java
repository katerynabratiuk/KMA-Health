package kma.health.app.kma_health.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchFormDto {
    private String searchType = "doctor";
    private String query;
    private String doctorType;
    private String city;
    private String sort = "rating-asc";
    private double userLat = 0;
    private double userLon = 0;
}

