package kma.health.app.kma_health.service;

import jakarta.persistence.EntityManager;
import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Hospital;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<Doctor> searchDoctors(DoctorSearchDto dto, double userLat, double userLon) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Doctor.class);
        var root = cq.from(Doctor.class);

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        if (dto.getDoctorType() != null && !dto.getDoctorType().isEmpty())
            predicates.add(cb.equal(root.get("doctorType").get("name"), dto.getDoctorType()));

        if (dto.getCity() != null && !dto.getCity().isEmpty())
            predicates.add(cb.equal(root.get("hospital").get("city"), dto.getCity()));

        if (dto.getHospitalId() != null)
            predicates.add(cb.equal(root.get("hospital").get("id"), dto.getHospitalId()));

        if (dto.getDoctorName() != null && !dto.getDoctorName().isEmpty())
            predicates.add(cb.like(cb.lower(root.get("fullName")), "%" + dto.getDoctorName().toLowerCase() + "%"));

        cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));

        List<Doctor> doctors = em.createQuery(cq).getResultList();

        DoctorSearchDto.SortBy sort = dto.getSortBy();
        String param = sort.param();
        String direction = sort.direction();

        if ("rating".equalsIgnoreCase(param)) {
            sortByRating(doctors, direction);
        } else if ("distance".equalsIgnoreCase(param)) {
            try {
                sortByDistance(doctors, direction, userLat, userLon);
            } catch (Exception e) {
                sortByRating(doctors, direction);
            }
        }

        return doctors;
    }

    private void sortByRating(List<Doctor> doctors, String direction) {
        doctors.sort((d1, d2) -> {
            double r1 = feedbackService.calculateDoctorRating(d1);
            double r2 = feedbackService.calculateDoctorRating(d2);

            return "asc".equalsIgnoreCase(direction)
                    ? Double.compare(r1, r2)
                    : Double.compare(r2, r1);
        });
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
