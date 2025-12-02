package kma.health.app.kma_health.service;

import kma.health.app.kma_health.entity.AuthUser;
import kma.health.app.kma_health.entity.Doctor;
import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.enums.UserRole;
import kma.health.app.kma_health.exception.InvalidCredentialsException;
import kma.health.app.kma_health.exception.RoleNotFoundException;
import kma.health.app.kma_health.repository.AuthUserRepository;
import kma.health.app.kma_health.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthUserRepository<Patient> patientRepository;

    @Mock
    private AuthUserRepository<Doctor> doctorRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        Map<UserRole, AuthUserRepository<? extends AuthUser>> repositories = new HashMap<>();
        repositories.put(UserRole.PATIENT, patientRepository);
        repositories.put(UserRole.DOCTOR, doctorRepository);
        authService = new AuthService(repositories, passwordEncoder, jwtUtils);
    }

    @Test
    void testLoginByEmail_Success() {
        UUID userId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(userId);
        patient.setEmail("test@example.com");
        patient.setPassword("encodedPassword");

        when(patientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(patient)).thenReturn("mock-token");

        String token = authService.loginByEmail("test@example.com", "password123", UserRole.PATIENT);

        assertEquals("mock-token", token);
        verify(jwtUtils).generateToken(patient);
    }

    @Test
    void testLoginByEmail_UserNotFound() {
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            authService.loginByEmail("notfound@example.com", "password", UserRole.PATIENT);
        });
    }

    @Test
    void testLoginByEmail_InvalidPassword() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setPassword("encodedPassword");

        when(patientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> {
            authService.loginByEmail("test@example.com", "wrongpassword", UserRole.PATIENT);
        });
    }

    @Test
    void testLoginByPhone_Success() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setPassword("encodedPassword");

        when(patientRepository.findByPhoneNumber("+380991234567")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(patient)).thenReturn("token");

        String token = authService.loginByPhone("+380991234567", "password", UserRole.PATIENT);

        assertEquals("token", token);
    }

    @Test
    void testLoginByPassport_Success() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setPassword("encodedPassword");

        when(patientRepository.findByPassportNumber("AB123456")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(patient)).thenReturn("token");

        String token = authService.loginByPassport("AB123456", "password", UserRole.PATIENT);

        assertEquals("token", token);
    }

    @Test
    void testLoginAny_SuccessWithEmail() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setPassword("encodedPassword");

        when(patientRepository.findByEmail("test@example.com")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(patient)).thenReturn("token");

        String token = authService.loginAny("test@example.com", "password", UserRole.PATIENT);

        assertEquals("token", token);
    }

    @Test
    void testLoginAny_FallbackToPhone() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setPassword("encodedPassword");

        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByPhoneNumber("+380991234567")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(patient)).thenReturn("token");

        String token = authService.loginAny("+380991234567", "password", UserRole.PATIENT);

        assertEquals("token", token);
    }

    @Test
    void testLoginAny_FallbackToPassport() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());
        patient.setPassword("encodedPassword");

        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByPassportNumber("AB123456")).thenReturn(Optional.of(patient));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(patient)).thenReturn("token");

        String token = authService.loginAny("AB123456", "password", UserRole.PATIENT);

        assertEquals("token", token);
    }

    @Test
    void testUpdateProfile_Success() {
        UUID userId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(userId);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        Map<String, String> updates = new HashMap<>();
        updates.put("email", "new@example.com");
        updates.put("password", "newPassword");
        updates.put("phoneNumber", "+380997654321");
        updates.put("passport", "CD789012");

        authService.updateProfile(userId, updates);

        assertEquals("new@example.com", patient.getEmail());
        assertEquals("encodedNewPassword", patient.getPassword());
        assertEquals("+380997654321", patient.getPhoneNumber());
        assertEquals("CD789012", patient.getPassportNumber());
        verify(patientRepository).save(patient);
    }

    @Test
    void testUpdateProfile_UnknownField() {
        UUID userId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(userId);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));

        Map<String, String> updates = new HashMap<>();
        updates.put("unknownField", "value");

        assertThrows(RuntimeException.class, () -> {
            authService.updateProfile(userId, updates);
        });
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        UUID userId = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        when(patientRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            authService.updateProfile(userId, new HashMap<>());
        });
    }

    @Test
    void testDeleteProfile_Success() {
        UUID userId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(userId);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));

        authService.deleteProfile(userId);

        verify(patientRepository).delete(patient);
    }

    @Test
    void testDeleteProfile_NoRole() {
        UUID userId = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        assertThrows(RuntimeException.class, () -> {
            authService.deleteProfile(userId);
        });
    }

    @Test
    void testExtractToken() {
        String authHeader = "Bearer my-jwt-token";
        String token = authService.extractToken(authHeader);
        assertEquals("my-jwt-token", token);
    }

    @Test
    void testGetUserFromToken_Patient() {
        String token = "mock-token";
        UUID userId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(userId);

        when(jwtUtils.getRoleFromToken(token)).thenReturn(UserRole.PATIENT);
        when(jwtUtils.getSubjectFromToken(token)).thenReturn(userId);
        when(patientRepository.getReferenceById(userId)).thenReturn(patient);

        AuthUser result = authService.getUserFromToken(token);

        assertEquals(patient, result);
    }

    @Test
    void testGetUserFromToken_Doctor() {
        String token = "mock-token";
        UUID userId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setId(userId);

        when(jwtUtils.getRoleFromToken(token)).thenReturn(UserRole.DOCTOR);
        when(jwtUtils.getSubjectFromToken(token)).thenReturn(userId);
        when(doctorRepository.getReferenceById(userId)).thenReturn(doctor);

        AuthUser result = authService.getUserFromToken(token);

        assertEquals(doctor, result);
    }

    @Test
    void testGetUserFromToken_UnsupportedRole() {
        String token = "mock-token";

        when(jwtUtils.getRoleFromToken(token)).thenReturn(UserRole.LAB_ASSISTANT);
        when(jwtUtils.getSubjectFromToken(token)).thenReturn(UUID.randomUUID());

        assertThrows(RoleNotFoundException.class, () -> {
            authService.getUserFromToken(token);
        });
    }

    @Test
    void testGetRepositoryByRole_NotFound() {
        Map<UserRole, AuthUserRepository<? extends AuthUser>> emptyRepositories = new HashMap<>();
        AuthService emptyService = new AuthService(emptyRepositories, passwordEncoder, jwtUtils);

        assertThrows(RuntimeException.class, () -> {
            emptyService.loginByEmail("test@example.com", "password", UserRole.PATIENT);
        });
    }

    @Test
    void testUpdateProfile_NoRoleFromAuthentication() {
        UUID userId = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        Map<String, String> updates = new HashMap<>();
        updates.put("email", "new@example.com");

        assertThrows(RuntimeException.class, () -> {
            authService.updateProfile(userId, updates);
        });
    }

    @Test
    void testLoginByPhone_UserNotFound() {
        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            authService.loginByPhone("+380991234567", "password", UserRole.PATIENT);
        });
    }

    @Test
    void testLoginByPassport_UserNotFound() {
        when(patientRepository.findByPassportNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            authService.loginByPassport("AB123456", "password", UserRole.PATIENT);
        });
    }

    @Test
    void testLoginAny_AllMethodsFail() {
        when(patientRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByPhoneNumber(anyString())).thenReturn(Optional.empty());
        when(patientRepository.findByPassportNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            authService.loginAny("invalid_identifier", "password", UserRole.PATIENT);
        });
    }

    @Test
    void testDeleteProfile_UserNotFound() {
        UUID userId = UUID.randomUUID();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        when(patientRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> {
            authService.deleteProfile(userId);
        });
    }

    @Test
    void testExtractToken_WithDifferentFormats() {
        String token1 = authService.extractToken("Bearer token-123");
        assertEquals("token-123", token1);
        
        String token2 = authService.extractToken("Bearer a.b.c");
        assertEquals("a.b.c", token2);
    }

    @Test
    void testLoginByEmail_AsDoctor() {
        UUID userId = UUID.randomUUID();
        Doctor doctor = new Doctor();
        doctor.setId(userId);
        doctor.setEmail("doctor@example.com");
        doctor.setPassword("encodedPassword");

        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(doctor));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(doctor)).thenReturn("doctor-token");

        String token = authService.loginByEmail("doctor@example.com", "password123", UserRole.DOCTOR);

        assertEquals("doctor-token", token);
    }

    @Test
    void testUpdateProfile_OnlyPassword() {
        UUID userId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(userId);
        patient.setEmail("original@example.com");

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        Map<String, String> updates = new HashMap<>();
        updates.put("password", "newPassword");

        authService.updateProfile(userId, updates);

        assertEquals("encodedNewPassword", patient.getPassword());
        assertEquals("original@example.com", patient.getEmail());
        verify(patientRepository).save(patient);
    }

    @Test
    void testUpdateProfile_AllFields() {
        UUID userId = UUID.randomUUID();
        Patient patient = new Patient();
        patient.setId(userId);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT")))
                .when(authentication).getAuthorities();
        SecurityContextHolder.setContext(securityContext);

        when(patientRepository.findById(userId)).thenReturn(Optional.of(patient));
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        Map<String, String> updates = new HashMap<>();
        updates.put("email", "new@email.com");
        updates.put("password", "pass");
        updates.put("phoneNumber", "+380999999999");
        updates.put("passport", "XY123456");

        authService.updateProfile(userId, updates);

        assertEquals("new@email.com", patient.getEmail());
        assertEquals("encodedPass", patient.getPassword());
        assertEquals("+380999999999", patient.getPhoneNumber());
        assertEquals("XY123456", patient.getPassportNumber());
    }
}

