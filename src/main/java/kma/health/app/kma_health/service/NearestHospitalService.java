package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.repository.HospitalRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
public class NearestHospitalService {

    private final HospitalRepository hospitalRepository;

    public Hospital findNearestHospital(String city, String latitude, String longitude) {
        LinkedList<Hospital> sortedHospitals = sortHospitalsByUserCoordinates(city, latitude, longitude);
        return sortedHospitals.isEmpty() ? null : sortedHospitals.getFirst();
    }

    public LinkedList<Hospital> sortHospitalsByUserCoordinates(String city, String latitude, String longitude) {
        List<Hospital> hospitals = hospitalRepository.findByCity(city);
        if (hospitals.isEmpty()) throw new EmptyResultDataAccessException("No hospitals found in this city", 1);

        double userLat;
        double userLon;

        try {
            userLat = Double.parseDouble(latitude);
            userLon = Double.parseDouble(longitude);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid latitude or longitude format");
        }

        hospitals.sort((h1, h2) -> {
            double d1 = distanceInKm(userLat, userLon, h1.getLatitude().doubleValue(), h1.getLongitude().doubleValue());
            double d2 = distanceInKm(userLat, userLon, h2.getLatitude().doubleValue(), h2.getLongitude().doubleValue());
            return Double.compare(d1, d2);
        });

        return new LinkedList<>(hospitals);
    }

    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double EARTH_RADIUS_KM = 6371.0;
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                   + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                     * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
