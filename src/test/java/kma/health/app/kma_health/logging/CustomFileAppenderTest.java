package kma.health.app.kma_health.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomFileAppenderTest {

    private CustomFileAppender appender;

    @BeforeEach
    void setUp() {
        appender = new CustomFileAppender();
    }

    @Test
    void testStart_WithoutFile() {
        appender.setName("testAppender");
        appender.setFile(null);

        appender.start();

        assertFalse(appender.isStarted());
    }

    @Test
    void testAppenderCreation() {
        assertNotNull(appender);
        assertNull(appender.getFile());
    }

    @Test
    void testSetName() {
        appender.setName("testAppender");
        assertEquals("testAppender", appender.getName());
    }
}

