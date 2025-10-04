package kma.health.app.kma_health.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import kma.health.app.kma_health.exception.CoordinatesNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class HospitalGeocodingService {

    private final RestTemplate restTemplate;

    @Autowired
    public HospitalGeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Coordinates getCoordinatesByAddress(String address) {
        String url = UriComponentsBuilder.fromHttpUrl("https://nominatim.openstreetmap.org/search")
                .queryParam("q", address)
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .toUriString();

        NominatimResponse[] response = restTemplate.getForObject(url, NominatimResponse[].class);

        if (response != null && response.length > 0) {
            double lat = Double.parseDouble(response[0].getLat());
            double lon = Double.parseDouble(response[0].getLon());
            return new Coordinates(lat, lon);
        } else {
            throw new CoordinatesNotFoundException("Couldn't get coordinates for address: " + address);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Coordinates {
        private double latitude;
        private double longitude;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class NominatimResponse {
        private String lat;
        private String lon;
    }
}
