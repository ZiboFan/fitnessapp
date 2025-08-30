package com.fitnessapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// All by myself
// Configuration class
@Configuration
public class SecurityConfig {

    // Define security filter chain
    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())// disable CSRF protection
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/", 
                "/register-login.html",
                "/main.html",
                "/workout-log.html",
                "/workout-history.html",
                "/profile.html",
                "/static/**",
                "/img/**",
                "/api/auth/**", 
                "/api/exercises/**",
                "/api/workouts/**",
                "/api/users/**"
            ).permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}

}
