package kma.health.app.kma_health.controller.api;

import kma.health.app.kma_health.config.KmaHealthCacheManager;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
@AllArgsConstructor
public class CacheController {

    KmaHealthCacheManager cacheManager;

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<?> clearCache(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return ResponseEntity.notFound().build();
        }
        cache.clear();
        return ResponseEntity.ok("Cache " + cacheName + " was cleared");
    }
}
