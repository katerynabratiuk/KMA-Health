package kma.health.app.kma_health.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCacheManagerIsLoaded() {
        assertNotNull(cacheManager);
    }

    @Test
    void testCacheNamesAreAvailable() {
        // Just verify cache manager is working
        assertNotNull(cacheManager.getCacheNames());
    }
}

