package com.agilis.api.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // rotas completamente públicas
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/*/coverage-areas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/*/availability").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/business-hours").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/*/units").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register/client").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/stores/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                        // todo o resto exige autenticação
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}