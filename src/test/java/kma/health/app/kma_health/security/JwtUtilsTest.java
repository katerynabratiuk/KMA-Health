package kma.health.app.kma_health.security;

import kma.health.app.kma_health.entity.Patient;
import kma.health.app.kma_health.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Set required properties via reflection
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", 
            "mySecretKeyForTestingPurposesWhichIsLongEnoughForHS512Algorithm123456789");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    void testGenerateToken_WithSubjectAndRole() {
        String subject = UUID.randomUUID().toString();
        String token = jwtUtils.generateToken(subject, UserRole.PATIENT);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateToken_WithAuthUser() {
        Patient patient = new Patient();
        patient.setId(UUID.randomUUID());

        String token = jwtUtils.generateToken(patient);

        assertNotNull(token);
    }

    @Test
    void testGetSubjectFromToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtUtils.generateToken(userId.toString(), UserRole.PATIENT);

        UUID extractedId = jwtUtils.getSubjectFromToken(token);

        assertEquals(userId, extractedId);
    }

    @Test
    void testGetRoleFromToken() {
        String token = jwtUtils.generateToken(UUID.randomUUID().toString(), UserRole.DOCTOR);

        UserRole role = jwtUtils.getRoleFromToken(token);

        assertEquals(UserRole.DOCTOR, role);
    }

    @Test
    void testValidateToken_ValidToken() {
        String token = jwtUtils.generateToken(UUID.randomUUID().toString(), UserRole.PATIENT);

        boolean isValid = jwtUtils.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        boolean isValid = jwtUtils.validateToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_MalformedToken() {
        boolean isValid = jwtUtils.validateToken("not-a-valid-jwt");

        assertFalse(isValid);
    }

    @Test
    void testValidateToken_EmptyToken() {
        boolean isValid = jwtUtils.validateToken("");

        assertFalse(isValid);
    }

    @Test
    void testGetExpirationDate() {
        String token = jwtUtils.generateToken(UUID.randomUUID().toString(), UserRole.PATIENT);

        Date expirationDate = jwtUtils.getExpirationDate(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testIsTokenExpired_NotExpired() {
        String token = jwtUtils.generateToken(UUID.randomUUID().toString(), UserRole.PATIENT);

        boolean isExpired = jwtUtils.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Create JwtUtils with very short expiration
        JwtUtils shortLivedJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortLivedJwtUtils, "jwtSecret", 
            "mySecretKeyForTestingPurposesWhichIsLongEnoughForHS512Algorithm123456789");
        ReflectionTestUtils.setField(shortLivedJwtUtils, "jwtExpirationMs", 1L); // 1ms

        String token = shortLivedJwtUtils.generateToken(UUID.randomUUID().toString(), UserRole.PATIENT);

        // Wait for token to expire
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean isValid = shortLivedJwtUtils.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void testGetAllClaimsFromToken_InvalidToken() {
        assertThrows(RuntimeException.class, () -> {
            jwtUtils.getSubjectFromToken("invalid.token");
        });
    }

    @Test
    void testValidateToken_NullToken() {
        boolean isValid = jwtUtils.validateToken(null);
        assertFalse(isValid);
    }
}

