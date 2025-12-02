package kma.health.app.kma_health.controllers;

import kma.health.app.kma_health.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testClearCache_Success() throws Exception {
        Cache mockCache = mock(Cache.class);
        when(cacheManager.getCache("testCache")).thenReturn(mockCache);

        mockMvc.perform(delete("/api/cache/testCache")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Cache testCache was cleared"));

        verify(mockCache).clear();
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void testClearCache_NotFound() throws Exception {
        when(cacheManager.getCache("nonExistentCache")).thenReturn(null);

        mockMvc.perform(delete("/api/cache/nonExistentCache")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void testClearCache_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/cache/testCache")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void testClearCache_Unauthorized() throws Exception {
        mockMvc.perform(delete("/api/cache/testCache")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}

