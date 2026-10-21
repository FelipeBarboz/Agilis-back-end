package com.agilis.api.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:3001,https://*.vercel.app,https://*.onrender.com}")
    private String allowedOrigins;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // requisições pre-flight do CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // rotas completamente públicas
                        .requestMatchers("/", "/error").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/coupons").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/coupons/validate").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/trending").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/top-rated").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/most-hired").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/service-area").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/support/messages").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/support/faq").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/verify-email").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/*/price-tiers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/*/coverage-areas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/*/availability").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/business-hours").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/units").permitAll()
                        .requestMatchers("/api/v1/stores/*/schedules/**").authenticated()
                        .requestMatchers("/api/v1/stores/*/positions/**").authenticated()
                        .requestMatchers("/api/v1/stores/*/webhooks/**").authenticated()
                        .requestMatchers("/api/v1/stores/*/invites/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register/client").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/store-invites/*").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                        // todo o resto exige autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition", "Link", "X-Total-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}