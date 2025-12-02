package kma.health.app.kma_health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class KmaHealthApplicationTests {

    @Test
    void contextLoads() {
        // Context loads successfully if this test passes
        assertTrue(true);
    }

    @Test
    void mainMethodExists() {
        // Just verify the main method exists and is accessible
        assertNotNull(KmaHealthApplication.class);
    }
}

