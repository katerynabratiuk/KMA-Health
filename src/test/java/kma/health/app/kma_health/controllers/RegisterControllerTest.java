package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.dto.DoctorRegisterRequest;
import kma.health.app.kma_health.dto.PatientRegisterRequest;
import kma.health.app.kma_health.security.JwtUtils;
import kma.health.app.kma_health.service.DoctorTypeService;
import kma.health.app.kma_health.service.HospitalService;
import kma.health.app.kma_health.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private HospitalService hospitalService;

    @MockitoBean
    private DoctorTypeService doctorTypeService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithAnonymousUser
    void testRegisterOptionsPage() throws Exception {
        mockMvc.perform(get("/ui/public/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-options"));
    }

    @Test
    @WithAnonymousUser
    void testDoctorRegisterPage() throws Exception {
        when(hospitalService.getAllHospitals()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/ui/public/register/doctor"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-doctor"))
                .andExpect(model().attributeExists("doctorRegisterRequest"));
    }

    @Test
    @WithAnonymousUser
    void testPatientRegisterPage() throws Exception {
        mockMvc.perform(get("/ui/public/register/patient"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-patient"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    @WithAnonymousUser
    void testRegisterPatient_Success() throws Exception {
        when(registrationService.register(any(PatientRegisterRequest.class))).thenReturn("token");

        mockMvc.perform(post("/ui/public/register/patient")
                        .with(csrf())
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("password", "password123")
                        .param("phoneNumber", "+380991234567")
                        .param("passportNumber", "123456789")
                        .param("birthDate", "1990-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/public/login"));
    }

    @Test
    @WithAnonymousUser
    void testRegisterPatient_ValidationError() throws Exception {
        mockMvc.perform(post("/ui/public/register/patient")
                        .with(csrf())
                        .param("fullName", "")
                        .param("email", "invalid"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-patient"));
    }

    @Test
    @WithAnonymousUser
    void testRegisterPatient_RuntimeError() throws Exception {
        when(registrationService.register(any(PatientRegisterRequest.class)))
                .thenThrow(new RuntimeException("Registration failed"));

        mockMvc.perform(post("/ui/public/register/patient")
                        .with(csrf())
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("password", "password123")
                        .param("phoneNumber", "+380991234567")
                        .param("passportNumber", "123456789")
                        .param("birthDate", "1990-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-patient"))
                .andExpect(model().attributeExists("globalError"));
    }

    @Test
    @WithAnonymousUser
    void testRegisterDoctor_Success() throws Exception {
        when(hospitalService.getAllHospitals()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypes()).thenReturn(Collections.emptyList());
        when(registrationService.register(any(DoctorRegisterRequest.class))).thenReturn("token");

        mockMvc.perform(post("/ui/public/register/doctor")
                        .with(csrf())
                        .param("fullName", "Dr. Smith")
                        .param("email", "dr.smith@example.com")
                        .param("password", "password123")
                        .param("phoneNumber", "+380997654321")
                        .param("passportNumber", "789012345")
                        .param("birthDate", "1980-05-15")
                        .param("type", "adult")
                        .param("description", "Experienced doctor")
                        .param("startedWorking", "2010-01-01")
                        .param("doctorTypeId", "1")
                        .param("hospitalId", "1")
                        .param("registerKey", "validkey"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ui/public/login"));
    }

    @Test
    @WithAnonymousUser
    void testRegisterDoctor_ValidationError() throws Exception {
        when(hospitalService.getAllHospitals()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypes()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/ui/public/register/doctor")
                        .with(csrf())
                        .param("fullName", "")
                        .param("email", "invalid"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-doctor"));
    }

    @Test
    @WithAnonymousUser
    void testRegisterDoctor_RuntimeError() throws Exception {
        when(hospitalService.getAllHospitals()).thenReturn(Collections.emptyList());
        when(doctorTypeService.getAllDoctorTypes()).thenReturn(Collections.emptyList());
        when(registrationService.register(any(DoctorRegisterRequest.class)))
                .thenThrow(new RuntimeException("Invalid registration key"));

        mockMvc.perform(post("/ui/public/register/doctor")
                        .with(csrf())
                        .param("fullName", "Dr. Smith")
                        .param("email", "dr.smith@example.com")
                        .param("password", "password123")
                        .param("phoneNumber", "+380997654321")
                        .param("passportNumber", "789012345")
                        .param("birthDate", "1980-05-15")
                        .param("type", "adult")
                        .param("description", "Experienced doctor")
                        .param("startedWorking", "2010-01-01")
                        .param("doctorTypeId", "1")
                        .param("hospitalId", "1")
                        .param("registerKey", "invalidkey"))
                .andExpect(status().isOk())
                .andExpect(view().name("register-doctor"))
                .andExpect(model().attributeExists("globalError"));
    }
}

