package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.controller.ui.RootRedirectController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RootRedirectControllerTest {

    @InjectMocks
    private RootRedirectController controller;

    @Test
    void testRedirectToHome() {
        String result = controller.redirectToHome();
        assertEquals("redirect:/ui/public/", result);
    }
}

