package kma.health.app.kma_health.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class KmaHealthCacheManager implements CacheManager {
    private final int DEFAULT_EXPIRE_MINUTES = 60;
    private final int MAX_CACHE_SIZE = 60;

    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, this::createCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }

    private Cache createCache(String name) {
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(MAX_CACHE_SIZE)
                .recordStats();

        switch (name) {
            case "doctorTypes", "doctorTypesNames":
                builder.expireAfterWrite(7, TimeUnit.DAYS);
                break;

            case "hospitals":
                builder.expireAfterWrite(1, TimeUnit.DAYS);
                break;

            default:
                builder.expireAfterWrite(DEFAULT_EXPIRE_MINUTES, TimeUnit.MINUTES);
                break;
        }

        return new CaffeineCache(name, builder.build());
    }
}
