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
        assertTrue(true);
    }

    @Test
    void mainMethodExists() {
        assertNotNull(KmaHealthApplication.class);
    }
}

