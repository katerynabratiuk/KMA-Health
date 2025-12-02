package kma.health.app.kma_health.dto;

import kma.health.app.kma_health.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    @Test
    void testLoginRequest_SettersAndGetters() {
        LoginRequest request = new LoginRequest();

        request.setIdentifier("test@example.com");
        request.setPassword("password123");
        request.setRole(UserRole.PATIENT);
        request.setMethod(LoginRequest.LoginMethod.EMAIL);

        assertEquals("test@example.com", request.getIdentifier());
        assertEquals("password123", request.getPassword());
        assertEquals(UserRole.PATIENT, request.getRole());
        assertEquals(LoginRequest.LoginMethod.EMAIL, request.getMethod());
    }

    @Test
    void testLoginMethod_Values() {
        LoginRequest.LoginMethod[] methods = LoginRequest.LoginMethod.values();

        assertEquals(3, methods.length);
        assertNotNull(LoginRequest.LoginMethod.valueOf("EMAIL"));
        assertNotNull(LoginRequest.LoginMethod.valueOf("PHONE"));
        assertNotNull(LoginRequest.LoginMethod.valueOf("PASSPORT"));
    }

    @Test
    void testLoginRequest_PhoneMethod() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("+380991234567");
        request.setMethod(LoginRequest.LoginMethod.PHONE);

        assertEquals(LoginRequest.LoginMethod.PHONE, request.getMethod());
    }

    @Test
    void testLoginRequest_PassportMethod() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("AB123456");
        request.setMethod(LoginRequest.LoginMethod.PASSPORT);

        assertEquals(LoginRequest.LoginMethod.PASSPORT, request.getMethod());
    }
}

