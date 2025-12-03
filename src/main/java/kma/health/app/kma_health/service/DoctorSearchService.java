package kma.health.app.kma_health.service;

import jakarta.persistence.EntityManager;
import kma.health.app.kma_health.dto.ReferralDto;
import kma.health.app.kma_health.dto.doctorDetail.DoctorDetailDto;
import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.repository.DoctorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static kma.health.app.kma_health.service.NearestHospitalService.distanceInKm;

@Service
@AllArgsConstructor
public class DoctorSearchService {

    private final EntityManager em;

    private final DoctorRepository doctorRepository;
    private final ReferralService referralService;
    private final FeedbackService feedbackService;

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
                            dto.getDoctorType().toLowerCase()));

        if (dto.getCity() != null && !dto.getCity().isEmpty()) {
            predicates.add(
                    cb.equal(
                            cb.lower(root.get("hospital").get("city")),
                            dto.getCity().toLowerCase()));
        }

        if (dto.getHospitalId() != null)
            predicates.add(cb.equal(root.get("hospital").get("id"), dto.getHospitalId()));

        if (dto.getQuery() != null && !dto.getQuery().isEmpty()) {
            predicates.add(
                    cb.like(
                            cb.lower(root.get("fullName")),
                            "%" + dto.getQuery().toLowerCase() + "%"));
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
            doctor.setType(doctor.getDoctorType().getTypeName());
            LocalDate startDate = doctor.getStartedWorking();
            int yearsOfExperience = Period.between(startDate, LocalDate.now()).getYears();
            doctor.setYearsOfExperience(yearsOfExperience);
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
                d -> d.getRating() == null ? 0.0 : d.getRating());
        if ("dsc".equalsIgnoreCase(direction))
            comparator = comparator.reversed();
        doctors.sort(comparator);
    }

    private void sortByDistance(List<Doctor> doctors, String direction,
                                double userLat, double userLon) {
        if (userLat == 0 && userLon == 0)
            throw new IllegalArgumentException("Invalid user lat/lon");
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

    public DoctorDetailDto getDoctorById(UUID id) {
        DoctorDetailDto doctor = new DoctorDetailDto(Objects.requireNonNull(doctorRepository.findById(id).orElse(null)));

        doctor.setRating(this.aggregatedRating(doctor.getFeedback()));
        doctor.setYearsOfExperience(countExperience(doctor.getStartedWorking()));

        return doctor;
    }

    public DoctorDetailDto getDoctorDetailById(UUID id, Optional<UUID> patientId) {
        DoctorDetailDto doctor = new DoctorDetailDto(Objects.requireNonNull(doctorRepository.findById(id).orElse(null)));
        doctor.setFeedback(feedbackService.getDoctorFeedbacks(doctor.getId()));

        doctor.setRating(this.aggregatedRating(doctor.getFeedback()));
        doctor.setYearsOfExperience(countExperience(doctor.getStartedWorking()));
        patientId.ifPresent(uuid -> doctor.setCanGetAppointment(patientCanGetAppointment(doctor, uuid)));
        patientId.ifPresent(uuid -> doctor.setCanRate(feedbackService.patientCanRateDoctor(id, patientId.get())));

        return doctor;
    }

    private Boolean patientCanGetAppointment(DoctorDetailDto doctor, UUID patientId) {
        if (doctor.getDoctorType().equals("Family Doctor")) return true;
        List<ReferralDto> activeReferrals = referralService.getActiveReferrals(patientId);

        return activeReferrals.stream().anyMatch(referral ->
                (referral.getDoctorId() != null &&
                 referral.getDoctorId().equals(doctor.getId())) ||

                (referral.getDoctorType() != null &&
                 referral.getDoctorType().equalsIgnoreCase(
                         doctor.getDoctorType()
                 ))
        );
    }


    private Double aggregatedRating(List<Feedback> feedback) {
        double avgRating = 0;
        if (feedback != null && !feedback.isEmpty()) {
            avgRating = feedback.stream()
                    .filter(f -> f.getScore() != null)
                    .mapToInt(kma.health.app.kma_health.entity.Feedback::getScore)
                    .average()
                    .orElse(0.0);
        }
        return avgRating;
    }

    private Integer countExperience(LocalDate startedWorking) {
        return java.time.Period.between(
                startedWorking,
                java.time.LocalDate.now()).getYears();
    }
}
