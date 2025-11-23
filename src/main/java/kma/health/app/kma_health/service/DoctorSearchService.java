package kma.health.app.kma_health.service;

import jakarta.persistence.EntityManager;
import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.logging.TimedInterruptible;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kma.health.app.kma_health.service.NearestHospitalService.distanceInKm;

@Service
@AllArgsConstructor
public class DoctorSearchService {

    private final EntityManager em;

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    public void setRatingService(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    //@TimedInterruptible(timeout = 600000)
    public List<Doctor> searchDoctors(DoctorSearchDto dto, double userLat, double userLon)
            throws InterruptedException {

        if (Thread.interrupted())
            throw new InterruptedException("Search interrupted before start");

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Doctor.class);
        var root = cq.from(Doctor.class);

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        if (dto.getDoctorType() != null && !dto.getDoctorType().isEmpty())
            predicates.add(
                    cb.equal(
                            cb.lower(root.get("doctorType").get("typeName")),
                            dto.getDoctorType().toLowerCase()
                    )
            );

        if (dto.getCity() != null && !dto.getCity().isEmpty()) {
            predicates.add(
                    cb.equal(
                            cb.lower(root.get("hospital").get("city")),
                            dto.getCity().toLowerCase()
                    )
            );
        }

        if (dto.getHospitalId() != null)
            predicates.add(cb.equal(root.get("hospital").get("id"), dto.getHospitalId()));

        if (dto.getQuery() != null && !dto.getQuery().isEmpty()) {
            predicates.add(
                    cb.like(
                            cb.lower(root.get("fullName")),
                            "%" + dto.getQuery().toLowerCase() + "%"
                    )
            );
        }

        cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));

        if (Thread.interrupted())
            throw new InterruptedException("Search interrupted before DB fetch");

        List<Doctor> doctors = em.createQuery(cq).getResultList();

        if (Thread.interrupted())
            throw new InterruptedException("Search interrupted after DB fetch");

        DoctorSearchDto.SortBy sort = dto.getSortBy();
        String param = sort.getParam();
        String direction = sort.getDirection();

        if ("rating".equalsIgnoreCase(param)) {
            if (Thread.interrupted())
                throw new InterruptedException("Interrupted before rating sort");
            sortByRating(doctors, direction);
        } else if ("distance".equalsIgnoreCase(param)) {
            if (Thread.interrupted())
                throw new InterruptedException("Interrupted before distance sort");
            try {
                sortByDistance(doctors, direction, userLat, userLon);
            } catch (Exception e) {
                sortByRating(doctors, direction);
            }
        }

        for (Doctor doctor : doctors) {
            if (Thread.interrupted())
                throw new InterruptedException("Interrupted during rating calculation");

            if (doctor.getFeedback() != null && !doctor.getFeedback().isEmpty()) {
                double avg = doctor.getFeedback().stream()
                        .filter(f -> f.getScore() != null)
                        .mapToInt(Feedback::getScore)
                        .average()
                        .orElse(0.0);

                doctor.setRating(avg);
            } else {
                doctor.setRating(0.0);
            }
        }

        if (Thread.interrupted())
            throw new InterruptedException("Interrupted after rating computation");

        return doctors;
    }

    private void sortByRating(List<Doctor> doctors, String direction) {
        for (Doctor doctor : doctors) {
            if (doctor.getRating() == null) {
                if (doctor.getFeedback() != null && !doctor.getFeedback().isEmpty()) {
                    double avg = doctor.getFeedback().stream()
                            .filter(f -> f.getScore() != null)
                            .mapToInt(Feedback::getScore)
                            .average()
                            .orElse(0.0);
                    doctor.setRating(avg);
                }
            }
        }
        Comparator<Doctor> comparator = Comparator.comparing(
                d -> d.getRating() == null ? 0.0 : d.getRating()
        );
        if ("dsc".equalsIgnoreCase(direction))
            comparator = comparator.reversed();
        doctors.sort(comparator);
    }


    private void sortByDistance(List<Doctor> doctors, String direction,
                                double userLat, double userLon) {
        if (userLat == 0 && userLon == 0) throw new IllegalArgumentException("Invalid user lat/lon");
        doctors.sort((d1, d2) -> {
            Hospital h1 = d1.getHospital();
            Hospital h2 = d2.getHospital();

            double dDist1 = distanceInKm(userLat, userLon,
                    h1.getLatitude(), h1.getLongitude());
            double dDist2 = distanceInKm(userLat, userLon,
                    h2.getLatitude(), h2.getLongitude());

            return "asc".equalsIgnoreCase(direction)
                    ? Double.compare(dDist1, dDist2)
                    : Double.compare(dDist2, dDist1);
        });
    }
}
