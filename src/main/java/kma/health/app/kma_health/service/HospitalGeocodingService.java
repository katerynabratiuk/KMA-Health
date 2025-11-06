package kma.health.app.kma_health.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kma.health.app.kma_health.exception.CoordinatesNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class HospitalGeocodingService {
    public Coordinates getCoordinatesByAddress(String address) {
        try {
            String encodedAddress = java.net.URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json&limit=1";
            ProcessBuilder pb = new ProcessBuilder(
                    "curl", "-s", "-A", "KMAHealthApp/1.0 (kmahealth@example.com)", url
            );
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            process.waitFor();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.toString());

            if (root.isArray() && !root.isEmpty()) {
                double lat = root.get(0).get("lat").asDouble();
                double lon = root.get(0).get("lon").asDouble();
                return new Coordinates(lat, lon);
            }
            else {
                throw new CoordinatesNotFoundException("Couldn't get coordinates for address: " + address);
            }
        }
        catch (Exception e) {
            throw new CoordinatesNotFoundException("Error while fetching coordinates: " + e.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    public static class Coordinates {
        private double latitude;
        private double longitude;
    }
}
