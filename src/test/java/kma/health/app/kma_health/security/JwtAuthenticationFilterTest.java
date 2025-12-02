package kma.health.app.kma_health.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kma.health.app.kma_health.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_NoCookies() throws Exception {
        when(request.getCookies()).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_NoJwtCookie() throws Exception {
        Cookie otherCookie = new Cookie("OTHER", "value");
        when(request.getCookies()).thenReturn(new Cookie[]{otherCookie});

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        Cookie jwtCookie = new Cookie("JWT", "invalid-token");
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtUtils.validateToken("invalid-token")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        String token = "valid-token";
        UUID userId = UUID.randomUUID();

        Cookie jwtCookie = new Cookie("JWT", token);
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getRoleFromToken(token)).thenReturn(UserRole.PATIENT);
        when(jwtUtils.getSubjectFromToken(token)).thenReturn(userId);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userId, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void testDoFilterInternal_DoctorRole() throws Exception {
        String token = "doctor-token";
        UUID userId = UUID.randomUUID();

        Cookie jwtCookie = new Cookie("JWT", token);
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getRoleFromToken(token)).thenReturn(UserRole.DOCTOR);
        when(jwtUtils.getSubjectFromToken(token)).thenReturn(userId);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")));
    }

    @Test
    void testDoFilterInternal_LabAssistantRole() throws Exception {
        String token = "lab-token";
        UUID userId = UUID.randomUUID();

        Cookie jwtCookie = new Cookie("JWT", token);
        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getRoleFromToken(token)).thenReturn(UserRole.LAB_ASSISTANT);
        when(jwtUtils.getSubjectFromToken(token)).thenReturn(userId);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LAB_ASSISTANT")));
    }

    @Test
    void testDoFilterInternal_MultipleCookies() throws Exception {
        String token = "valid-token";
        UUID userId = UUID.randomUUID();

        Cookie cookie1 = new Cookie("OTHER1", "value1");
        Cookie jwtCookie = new Cookie("JWT", token);
        Cookie cookie2 = new Cookie("OTHER2", "value2");

        when(request.getCookies()).thenReturn(new Cookie[]{cookie1, jwtCookie, cookie2});
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.getRoleFromToken(token)).thenReturn(UserRole.PATIENT);
        when(jwtUtils.getSubjectFromToken(token)).thenReturn(userId);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

