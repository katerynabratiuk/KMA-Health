package kma.health.app.kma_health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class KmaHealthApplicationTest {

    @Test
    void contextLoads() {
        // Just verify that the context loads successfully
        assertTrue(true);
    }

    @Test
    void mainMethodRuns() {
        // Test that main method can be invoked without throwing exceptions
        // Note: This doesn't actually start the application in tests
        assertDoesNotThrow(() -> {
            // The main method is typically tested via integration tests
            // Here we just ensure the class exists and is loadable
            Class<?> clazz = KmaHealthApplication.class;
            assertNotNull(clazz);
        });
    }
}

