package kma.health.app.kma_health.service;

import jakarta.persistence.EntityManager;
import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.dto.HospitalSearchDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.enums.FeedbackTargetType;
import kma.health.app.kma_health.logging.TimedInterruptible;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HospitalSearchService {

    private final EntityManager em;
    private final FeedbackService feedbackService;

    //@TimedInterruptible(timeout = 150)
    public List<Hospital> searchHospitals(HospitalSearchDto dto, double userLat, double userLon)
            throws InterruptedException {

        if (Thread.interrupted())
            throw new InterruptedException("Hospital search interrupted before start");

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Hospital.class);
        var root = cq.from(Hospital.class);

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        if (dto.getRequest() != null && !dto.getRequest().isEmpty()) {
            String req = "%" + dto.getRequest().toLowerCase() + "%";
            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), req),
                    cb.like(cb.lower(root.get("address")), req)
            ));
        }

        if (dto.getCity() != null && !dto.getCity().isEmpty())
            predicates.add(cb.equal(cb.lower(root.get("city")), dto.getCity().toLowerCase()));
        if (dto.getHospitalType() != null)
            predicates.add(cb.equal(root.get("hospitalType"), dto.getHospitalType()));
        cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));

        if (Thread.interrupted())
            throw new InterruptedException("Hospital search interrupted before DB query");

        List<Hospital> hospitals = em.createQuery(cq).getResultList();

        if (Thread.interrupted())
            throw new InterruptedException("Hospital search interrupted after DB fetch");

        DoctorSearchDto.SortBy sort = dto.getSortBy();
        String param = sort.getParam();
        String direction = sort.getDirection();

        if ("rating".equalsIgnoreCase(param)) {
            if (Thread.interrupted())
                throw new InterruptedException("Interrupted before sorting hospitals by rating");
            sortByRating(hospitals, direction);
        } else if ("distance".equalsIgnoreCase(param)) {
            if (Thread.interrupted())
                throw new InterruptedException("Interrupted before sorting hospitals by distance");
            try {
                sortByDistance(hospitals, direction, userLat, userLon);
            } catch (Exception e) {
                sortByRating(hospitals, direction);
            }
        }

        System.out.println(hospitals);

        for (Hospital hospital : hospitals) {
            if (Thread.interrupted())
                throw new InterruptedException("Interrupted during hospital rating calculation");
            if (hospital.getFeedback() != null && !hospital.getFeedback().isEmpty()) {
                double avg = hospital.getFeedback().stream()
                        .filter(f -> f.getTargetType() == FeedbackTargetType.HOSPITAL)
                        .filter(f -> f.getScore() != null)
                        .mapToInt(Feedback::getScore)
                        .average()
                        .orElse(0.0);

                hospital.setRating(avg);
            } else {
                hospital.setRating(0.0);
            }
        }

        if (Thread.interrupted())
            throw new InterruptedException("Interrupted after hospital rating computation");
        System.out.println(hospitals);

        return hospitals;
    }

    private void sortByRating(List<Hospital> hospitals, String direction) {
        hospitals.sort((h1, h2) -> {
            double r1 = h1.getRating() != null ? h1.getRating() : 0.0;
            double r2 = h2.getRating() != null ? h2.getRating() : 0.0;
            return "dsc".equalsIgnoreCase(direction)
                    ? Double.compare(r2, r1)
                    : Double.compare(r1, r2);
        });
    }

    private void sortByDistance(List<Hospital> hospitals, String direction, double userLat, double userLon) {
        System.out.println("Sorting by distance (" + direction + ")");
        hospitals.sort((h1, h2) -> {
            double d1 = distance(userLat, userLon, h1.getLatitude(), h1.getLongitude());
            double d2 = distance(userLat, userLon, h2.getLatitude(), h2.getLongitude());
            System.out.printf("Compare: %s(%.2f km) vs %s(%.2f km)%n",
                    h1.getName(), d1, h2.getName(), d2);
            return "dsc".equalsIgnoreCase(direction)
                    ? Double.compare(d2, d1)
                    : Double.compare(d1, d2);
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                   + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                     * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); // km
    }
}
