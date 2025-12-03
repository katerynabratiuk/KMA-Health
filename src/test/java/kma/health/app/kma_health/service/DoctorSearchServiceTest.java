package kma.health.app.kma_health.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import kma.health.app.kma_health.dto.DoctorSearchDto;
import kma.health.app.kma_health.dto.doctorDetail.DoctorDetailDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.entity.Feedback;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import kma.health.app.kma_health.dto.ReferralDto;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DoctorSearchServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private ReferralService referralService;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private PatientService patientService;

    @Mock
    private DoctorTypeService doctorTypeService;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Doctor> criteriaQuery;

    @Mock
    private Root<Doctor> root;

    @Mock
    private TypedQuery<Doctor> typedQuery;

    @Mock
    private Path<Object> path;

    @Mock
    private Path<String> stringPath;

    @Mock
    private Expression<String> lowerExpression;

    private DoctorSearchService doctorSearchService;

    @BeforeEach
    void setUp() {
        doctorSearchService = new DoctorSearchService(entityManager, doctorRepository, referralService, feedbackService, patientService, doctorTypeService);
    }

    @Test
    void testSearchDoctors_WithAllFilters() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setDoctorType("Cardiologist");
        dto.setCity("Kyiv");
        dto.setHospitalId(1L);
        dto.setQuery("Smith");
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "dsc"));

        setupCriteriaMocks();

        Doctor doctor = createMockDoctor();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchDoctors_SortByDistance() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "asc"));

        setupCriteriaMocks();

        Doctor doctor1 = createMockDoctorWithHospital(50.0, 30.0);
        Doctor doctor2 = createMockDoctorWithHospital(51.0, 31.0);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor1, doctor2)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchDoctors_SortByDistanceWithInvalidCoordinates() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "asc"));

        setupCriteriaMocks();

        Doctor doctor = createMockDoctor();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 0, 0);

        assertNotNull(result);
    }

    @Test
    void testSearchDoctors_WithNullFeedback() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));
        doctor.setFeedback(null);
        
        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);
        
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);
        
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertEquals(0.0, result.get(0).getRating());
    }

    @Test
    void testSearchDoctors_WithEmptyFeedback() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));
        doctor.setFeedback(new ArrayList<>());
        
        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);
        
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);
        
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertEquals(0.0, result.get(0).getRating());
    }

    @Test
    void testSearchDoctors_WithNullRatingInSort() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "dsc"));

        setupCriteriaMocks();

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        
        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");

        Doctor doctor1 = new Doctor();
        doctor1.setId(UUID.randomUUID());
        doctor1.setStartedWorking(LocalDate.of(2020, 1, 1));
        doctor1.setRating(null);
        doctor1.setFeedback(null);
        doctor1.setDoctorType(doctorType);
        doctor1.setHospital(hospital);

        Doctor doctor2 = new Doctor();
        doctor2.setId(UUID.randomUUID());
        doctor2.setStartedWorking(LocalDate.of(2019, 1, 1));
        doctor2.setRating(4.5);
        doctor2.setDoctorType(doctorType);
        doctor2.setHospital(hospital);

        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor1, doctor2)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testGetDoctorById_Found() {
        UUID doctorId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        DoctorDetailDto result = doctorSearchService.getDoctorById(doctorId);

        assertNotNull(result);
        assertEquals(doctorId, result.getId());
    }

    @Test
    void testGetDoctorById_NotFound() {
        UUID doctorId = UUID.randomUUID();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        assertThrows(NullPointerException.class, () -> doctorSearchService.getDoctorById(doctorId));
    }

    @Test
    void testSearchDoctors_InterruptedBeforeStart() {
        Thread.currentThread().interrupt();

        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        assertThrows(InterruptedException.class, () -> {
            doctorSearchService.searchDoctors(dto, 50.45, 30.52);
        });

        Thread.interrupted();
    }

    private void setupCriteriaMocks() {
        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Doctor.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Doctor.class)).thenReturn(root);
        when(criteriaQuery.where(any(Predicate[].class))).thenReturn(criteriaQuery);
        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);

        when(root.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
        when(criteriaBuilder.lower(any())).thenReturn(lowerExpression);
        when(criteriaBuilder.equal(any(), any())).thenReturn(mock(Predicate.class));
        when(criteriaBuilder.like(any(Expression.class), anyString())).thenReturn(mock(Predicate.class));
    }

    private Doctor createMockDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setFullName("Dr. Smith");
        doctor.setStartedWorking(LocalDate.of(2015, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Feedback feedback = new Feedback();
        feedback.setScore((short) 5);
        doctor.setFeedback(List.of(feedback));

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        hospital.setLatitude(50.45);
        hospital.setLongitude(30.52);
        doctor.setHospital(hospital);

        return doctor;
    }

    private Doctor createMockDoctorWithHospital(double lat, double lon) {
        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setStartedWorking(LocalDate.of(2015, 1, 1));
        doctor.setFeedback(new ArrayList<>());

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        hospital.setLatitude(lat);
        hospital.setLongitude(lon);
        doctor.setHospital(hospital);

        return doctor;
    }

    @Test
    void testSearchDoctors_SortByDistanceDescending() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("distance", "dsc"));

        setupCriteriaMocks();

        Doctor doctor1 = createMockDoctorWithHospital(50.0, 30.0);
        Doctor doctor2 = createMockDoctorWithHospital(51.0, 31.0);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor1, doctor2)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testSearchDoctors_SortByRatingAscending() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Doctor doctor1 = createMockDoctor();
        doctor1.setRating(3.0);
        Doctor doctor2 = createMockDoctor();
        doctor2.setRating(5.0);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor1, doctor2)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testGetDoctorDetailById_WithPatientId() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));
        doctor.setFullName("Dr. Test");

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(new ArrayList<>());
        when(referralService.getActiveReferrals(patientId)).thenReturn(new ArrayList<>());
        when(feedbackService.patientCanRateDoctor(doctorId, patientId)).thenReturn(true);

        DoctorDetailDto result = doctorSearchService.getDoctorDetailById(doctorId, Optional.of(patientId));

        assertNotNull(result);
        assertEquals(doctorId, result.getId());
    }

    @Test
    void testGetDoctorDetailById_WithoutPatientId() {
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(new ArrayList<>());

        DoctorDetailDto result = doctorSearchService.getDoctorDetailById(doctorId, Optional.empty());

        assertNotNull(result);
    }

    @Test
    void testGetDoctorDetailById_PatientCanGetAppointment_ByDoctorId() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        ReferralDto referral = new ReferralDto();
        referral.setDoctorId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(new ArrayList<>());
        when(referralService.getActiveReferrals(patientId)).thenReturn(List.of(referral));
        when(feedbackService.patientCanRateDoctor(doctorId, patientId)).thenReturn(false);

        DoctorDetailDto result = doctorSearchService.getDoctorDetailById(doctorId, Optional.of(patientId));

        assertNotNull(result);
        assertTrue(result.getCanGetAppointment());
    }

    @Test
    void testGetDoctorDetailById_PatientCanGetAppointment_ByDoctorType() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        ReferralDto referral = new ReferralDto();
        referral.setDoctorType("Cardiologist");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(new ArrayList<>());
        when(referralService.getActiveReferrals(patientId)).thenReturn(List.of(referral));
        when(feedbackService.patientCanRateDoctor(doctorId, patientId)).thenReturn(false);

        DoctorDetailDto result = doctorSearchService.getDoctorDetailById(doctorId, Optional.of(patientId));

        assertNotNull(result);
        assertTrue(result.getCanGetAppointment());
    }

    @Test
    void testGetDoctorDetailById_PatientCannotGetAppointment() {
        UUID doctorId = UUID.randomUUID();
        UUID patientId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        ReferralDto referral = new ReferralDto();
        referral.setDoctorType("Neurologist");

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(new ArrayList<>());
        when(referralService.getActiveReferrals(patientId)).thenReturn(List.of(referral));
        when(feedbackService.patientCanRateDoctor(doctorId, patientId)).thenReturn(false);

        DoctorDetailDto result = doctorSearchService.getDoctorDetailById(doctorId, Optional.of(patientId));

        assertNotNull(result);
        assertFalse(result.getCanGetAppointment());
    }

    @Test
    void testSearchDoctors_WithFeedbackHavingNullScore() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Doctor doctor = new Doctor();
        doctor.setId(UUID.randomUUID());
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));

        Feedback feedbackWithScore = new Feedback();
        feedbackWithScore.setScore((short) 5);

        Feedback feedbackWithoutScore = new Feedback();
        feedbackWithoutScore.setScore(null);

        doctor.setFeedback(List.of(feedbackWithScore, feedbackWithoutScore));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(5.0, result.get(0).getRating());
    }

    @Test
    void testSearchDoctors_WithNoFilters() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setDoctorType(null);
        dto.setCity(null);
        dto.setHospitalId(null);
        dto.setQuery(null);
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Doctor doctor = createMockDoctor();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testSearchDoctors_WithEmptyFilters() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setDoctorType("");
        dto.setCity("");
        dto.setQuery("");
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        Doctor doctor = createMockDoctor();
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
    }

    @Test
    void testGetDoctorDetailById_WithFeedback() {
        UUID doctorId = UUID.randomUUID();

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setStartedWorking(LocalDate.of(2020, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        doctor.setHospital(hospital);

        Feedback feedback = new Feedback();
        feedback.setScore((short) 4);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(feedbackService.getDoctorFeedbacks(doctorId)).thenReturn(List.of(feedback));

        DoctorDetailDto result = doctorSearchService.getDoctorDetailById(doctorId, Optional.empty());

        assertNotNull(result);
        assertEquals(4.0, result.getRating());
    }

    @Test
    void testSearchDoctors_SortByRatingWithNullRatingInSortByRatingMethod() throws InterruptedException {
        DoctorSearchDto dto = new DoctorSearchDto();
        dto.setSortBy(new DoctorSearchDto.SortBy("rating", "asc"));

        setupCriteriaMocks();

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");

        Doctor doctor1 = new Doctor();
        doctor1.setId(UUID.randomUUID());
        doctor1.setStartedWorking(LocalDate.of(2020, 1, 1));
        doctor1.setRating(null);
        Feedback feedback1 = new Feedback();
        feedback1.setScore((short) 4);
        doctor1.setFeedback(List.of(feedback1));
        doctor1.setDoctorType(doctorType);
        doctor1.setHospital(hospital);

        Doctor doctor2 = new Doctor();
        doctor2.setId(UUID.randomUUID());
        doctor2.setStartedWorking(LocalDate.of(2019, 1, 1));
        doctor2.setRating(3.0);
        doctor2.setFeedback(new ArrayList<>());
        doctor2.setDoctorType(doctorType);
        doctor2.setHospital(hospital);

        when(typedQuery.getResultList()).thenReturn(new ArrayList<>(List.of(doctor1, doctor2)));

        List<Doctor> result = doctorSearchService.searchDoctors(dto, 50.45, 30.52);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}

