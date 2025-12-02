package kma.health.app.kma_health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class KmaHealthApplicationTests {

    @Test
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    void mainMethodRuns() {
        // Test the main method by invoking it with no args
        // Since context is already loaded, this test just ensures the main method exists
        assertDoesNotThrow(() -> {
            KmaHealthApplication.main(new String[]{});
        });
    }
}

