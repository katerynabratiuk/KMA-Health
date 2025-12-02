package kma.health.app.kma_health.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;

import static org.junit.jupiter.api.Assertions.*;

public class KmaHealthCacheManagerTest {

    private KmaHealthCacheManager cacheManager;

    @BeforeEach
    void setUp() {
        cacheManager = new KmaHealthCacheManager();
    }

    @Test
    void testGetCache_DefaultCache() {
        Cache cache = cacheManager.getCache("testCache");
        assertNotNull(cache);
        assertEquals("testCache", cache.getName());
    }

    @Test
    void testGetCache_DoctorTypesCache() {
        Cache cache = cacheManager.getCache("doctorTypes");
        assertNotNull(cache);
        assertEquals("doctorTypes", cache.getName());
    }

    @Test
    void testGetCache_DoctorTypesNamesCache() {
        Cache cache = cacheManager.getCache("doctorTypesNames");
        assertNotNull(cache);
        assertEquals("doctorTypesNames", cache.getName());
    }

    @Test
    void testGetCache_HospitalsCache() {
        Cache cache = cacheManager.getCache("hospitals");
        assertNotNull(cache);
        assertEquals("hospitals", cache.getName());
    }

    @Test
    void testGetCache_ReturnsSameCacheForSameName() {
        Cache cache1 = cacheManager.getCache("testCache");
        Cache cache2 = cacheManager.getCache("testCache");
        assertSame(cache1, cache2);
    }

    @Test
    void testGetCacheNames_ReturnsAllCacheNames() {
        cacheManager.getCache("cache1");
        cacheManager.getCache("cache2");
        cacheManager.getCache("cache3");

        var names = cacheManager.getCacheNames();

        assertEquals(3, names.size());
        assertTrue(names.contains("cache1"));
        assertTrue(names.contains("cache2"));
        assertTrue(names.contains("cache3"));
    }

    @Test
    void testGetCacheNames_EmptyInitially() {
        var names = cacheManager.getCacheNames();
        assertTrue(names.isEmpty());
    }

    @Test
    void testCache_PutAndGet() {
        Cache cache = cacheManager.getCache("testCache");
        cache.put("key1", "value1");

        Cache.ValueWrapper wrapper = cache.get("key1");
        assertNotNull(wrapper);
        assertEquals("value1", wrapper.get());
    }

    @Test
    void testCache_Clear() {
        Cache cache = cacheManager.getCache("testCache");
        cache.put("key1", "value1");
        cache.clear();

        Cache.ValueWrapper wrapper = cache.get("key1");
        assertNull(wrapper);
    }
}

