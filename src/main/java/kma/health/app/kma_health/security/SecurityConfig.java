package kma.health.app.kma_health.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/ui/public/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        // patient API
                        .requestMatchers("/api/patient/**").hasRole("PATIENT")

                        // feedback
                        .requestMatchers(HttpMethod.POST, "/api/doctor/*/feedback").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET,  "/api/doctor/*/feedback").permitAll()

                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")

                        .requestMatchers("/api/lab/**").hasRole("LAB_ASSISTANT")

                        .requestMatchers(HttpMethod.GET, "/api/hospital").permitAll()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
