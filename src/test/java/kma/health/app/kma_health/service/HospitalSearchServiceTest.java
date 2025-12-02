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

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setFeedback(new ArrayList<>());
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

        Thread.interrupted();
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

    @Test
    void testSearchHospitals_WithNullRatingInSorting() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);
        hospital1.setLatitude(50.0);
        hospital1.setLongitude(30.0);
        hospital1.setRating(null);
        hospital1.setFeedback(new ArrayList<>());

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setLatitude(51.0);
        hospital2.setLongitude(31.0);
        hospital2.setRating(4.5);
        hospital2.setFeedback(new ArrayList<>());

        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital1, hospital2)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchHospitals_WithFeedbackFiltering() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Feedback hospitalFeedback = new Feedback();
        hospitalFeedback.setScore((short) 4);
        hospitalFeedback.setTargetType(FeedbackTargetType.HOSPITAL);

        Feedback doctorFeedback = new Feedback();
        doctorFeedback.setScore((short) 2);
        doctorFeedback.setTargetType(FeedbackTargetType.DOCTOR);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setLatitude(50.0);
        hospital.setLongitude(30.0);
        hospital.setFeedback(List.of(hospitalFeedback, doctorFeedback));

        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(4.0, result.get(0).getRating());
    }

    @Test
    void testSearchHospitals_WithFeedbackHavingNullScore() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Feedback feedbackWithScore = new Feedback();
        feedbackWithScore.setScore((short) 4);
        feedbackWithScore.setTargetType(FeedbackTargetType.HOSPITAL);

        Feedback feedbackWithNullScore = new Feedback();
        feedbackWithNullScore.setScore(null);
        feedbackWithNullScore.setTargetType(FeedbackTargetType.HOSPITAL);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setLatitude(50.0);
        hospital.setLongitude(30.0);
        hospital.setFeedback(List.of(feedbackWithScore, feedbackWithNullScore));

        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(4.0, result.get(0).getRating());
    }

    @Test
    void testSearchHospitals_WithOnlyRequestFilter() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setRequest("City Hospital");
        dto.setCity(null);
        dto.setHospitalType(null);
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital = createMockHospital();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_WithEmptyRequest() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setRequest("");
        dto.setCity("");
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital = createMockHospital();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_WithOnlyCityFilter() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setCity("Kyiv");
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital = createMockHospital();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_WithOnlyHospitalTypeFilter() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setHospitalType(HospitalType.PRIVATE);
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital = createMockHospital();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchHospitals_NoFeedbackSetsZeroRating() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Hospital hospital1 = new Hospital();
        hospital1.setId(1L);
        hospital1.setLatitude(50.0);
        hospital1.setLongitude(30.0);
        hospital1.setFeedback(null);

        Hospital hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setLatitude(51.0);
        hospital2.setLongitude(31.0);
        hospital2.setFeedback(new ArrayList<>());

        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital1, hospital2)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertEquals(0.0, result.get(0).getRating());
        assertEquals(0.0, result.get(1).getRating());
    }

    @Test
    void testSearchHospitals_RatingSortDescending() throws InterruptedException {
        HospitalSearchDto dto = new HospitalSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "dsc"));

        setupCriteriaMocks();

        Hospital hospital1 = createMockHospitalWithRating(3.0);
        Hospital hospital2 = createMockHospitalWithRating(5.0);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(hospital1, hospital2)));

        List<Hospital> result = hospitalSearchService.searchHospitals(dto, 50.45, 30.52);

        assertNotNull(result);
    }
}

