package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.ui.AuthController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController controller;

    @Test
    void testLogin() {
        String result = controller.login();
        assertEquals("login", result);
    }
}

