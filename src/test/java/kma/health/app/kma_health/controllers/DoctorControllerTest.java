package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.ui.DoctorController;
import kma.health.app.kma_health.dto.doctorDetail.DoctorDetailDto;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.DoctorType;
import kma.health.app.kma_health.entity.Hospital;
import kma.health.app.kma_health.service.DoctorSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoctorControllerTest {

    @Mock
    private DoctorSearchService doctorSearchService;

    @Mock
    private Model model;

    @InjectMocks
    private DoctorController controller;

    private UUID doctorId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        userId = UUID.randomUUID();
        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testDoctorDetail_AnonymousUser_Success() {
        DoctorDetailDto doctorDto = createMockDoctorDetailDto(doctorId);

        when(doctorSearchService.getDoctorDetailById(eq(doctorId), any())).thenReturn(doctorDto);

        String result = controller.doctorDetail(doctorId, null, model);

        assertEquals("doctor-detail", result);
        verify(model).addAttribute("doctor", doctorDto);
        verify(model).addAttribute(eq("userRole"), any());
    }

    @Test
    void testDoctorDetail_AuthenticatedPatient_Success() {
        setSecurityContext("PATIENT");
        DoctorDetailDto doctorDto = createMockDoctorDetailDto(doctorId);

        when(doctorSearchService.getDoctorDetailById(eq(doctorId), eq(Optional.of(userId)))).thenReturn(doctorDto);

        String result = controller.doctorDetail(doctorId, userId, model);

        assertEquals("doctor-detail", result);
        verify(model).addAttribute("doctor", doctorDto);
        verify(model).addAttribute("userRole", "PATIENT");
    }

    @Test
    void testDoctorDetail_AuthenticatedDoctor_Success() {
        setSecurityContext("DOCTOR");
        DoctorDetailDto doctorDto = createMockDoctorDetailDto(doctorId);

        when(doctorSearchService.getDoctorDetailById(eq(doctorId), eq(Optional.of(userId)))).thenReturn(doctorDto);

        String result = controller.doctorDetail(doctorId, userId, model);

        assertEquals("doctor-detail", result);
        verify(model).addAttribute("userRole", "DOCTOR");
    }

    @Test
    void testDoctorDetail_DoctorNotFound_Redirects() {
        setSecurityContext("PATIENT");

        when(doctorSearchService.getDoctorDetailById(eq(doctorId), eq(Optional.of(userId)))).thenReturn(null);

        String result = controller.doctorDetail(doctorId, userId, model);

        assertEquals("redirect:/ui/public/", result);
    }

    private DoctorDetailDto createMockDoctorDetailDto(UUID doctorId) {
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setFullName("Dr. Test");
        doctor.setStartedWorking(LocalDate.of(2015, 1, 1));

        DoctorType doctorType = new DoctorType();
        doctorType.setTypeName("Cardiologist");
        doctor.setDoctorType(doctorType);

        Hospital hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        doctor.setHospital(hospital);

        return new DoctorDetailDto(doctor);
    }
}
