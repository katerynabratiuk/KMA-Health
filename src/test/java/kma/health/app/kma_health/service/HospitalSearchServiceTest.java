package kma.health.app.kma_health.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.dto.HospitalSearchDto;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.enums.FeedbackTargetType;
import kma.health.app.kma_health.enums.HospitalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HospitalSearchServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Hospital> criteriaQuery;

    @Mock
    private Root<Hospital> root;

    @Mock
    private TypedQuery<Hospital> typedQuery;

    @Mock
    private Path<Object> path;

    @Mock
    private Expression<String> lowerExpression;

    private HospitalSearchService hospitalSearchService;

    @BeforeEach
    void setUp() {
        hospitalSearchService = new HospitalSearchService(entityManager);
    }

    @Test
    void testSearchHospitals_WithAllFilters() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setRequest("Test Hospital");
        dto.setCity("Kyiv");
        dto.setHospitalType(HospitalType.PUBLIC);
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "dsc"));

        setupCriteriaMocks();

        Hospital hospital = createMockHospital();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchHospitals_SortByDistance() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "asc"));

        setupCriteriaMocks();

        Hospital hospital1 = createMockHospitalWithCoordinates(50.0, 30.0);
        Hospital hospital2 = createMockHospitalWithCoordinates(51.0, 31.0);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital1, hospital2)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_SortByDistanceDescending() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "dsc"));

        setupCriteriaMocks();

        Hospital hospital1 = createMockHospitalWithCoordinates(50.0, 30.0);
        Hospital hospital2 = createMockHospitalWithCoordinates(51.0, 31.0);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital1, hospital2)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_SortByRatingAscending() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital1 = createMockHospitalWithRating(3.0);
        Hospital hospital2 = createMockHospitalWithRating(5.0);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital1, hospital2)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_WithNullFeedback() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setLatitude(50.0);
        hospital.setLongitude(30.0);
        hospital.setFeedback(null);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertEquals(0.0, result.get(0).getRating());
    }

    @Test
    void testSearchHospitals_WithEmptyFeedback() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setLatitude(50.0);
        hospital.setLongitude(30.0);
        hospital.setFeedback(new ArrayList<>());
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertEquals(0.0, result.get(0).getRating());
    }

    @Test
    void testSearchHospitals_DistanceSortFallsBackToRating() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "asc"));

        setupCriteriaMocks();

        // Hospital with null coordinates will cause exception in distance calculation
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setFeedback(new ArrayList<>());
        // No lat/lon set - will cause NullPointerException in distance calc
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_Interrupted() {
        Thread.currentThread().interrupt();

        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        assertThrows(InterruptedException.class, () -> {
            hospitalSearchService.searchHospitals(dto, 50.45, 30.52);
        });

        Thread.interrupted(); // Clear interrupt flag
    }

    private void setupCriteriaMocks() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Hospital.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Hospital.class)).thenReturn(root);
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        when(root.get(anyString())).thenReturn(path);
        when(criteriaBuilder.lower(any())).thenReturn(lowerExpression);
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.or(any(), any())).thenReturn(mock(Predicate.class));
    }

    private Hospital createMockHospital() {
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setLatitude(50.45);
        hospital.setLongitude(30.52);

        Feedback feedback = new Feedback();
        feedback.setScore((short) 5);
        feedback.setTargetType(FeedbackTargetType.HOSPITAL);
        hospital.setFeedback(List.of(feedback));

        return hospital;
    }

    private Hospital createMockHospitalWithCoordinates(double lat, double lon) {
        Hospital hospital = new Hospital();
        hospital.setId((long) (lat * 100));
        hospital.setName("Hospital at " + lat + ", " + lon);
        hospital.setLatitude(lat);
        hospital.setLongitude(lon);
        hospital.setFeedback(new ArrayList<>());
        return hospital;
    }

    private Hospital createMockHospitalWithRating(double rating) {
        Hospital hospital = new Hospital();
        hospital.setId((long) (rating * 100));
        hospital.setLatitude(50.0);
        hospital.setLongitude(30.0);
        hospital.setRating(rating);
        hospital.setFeedback(new ArrayList<>());
        return hospital;
    }
}

