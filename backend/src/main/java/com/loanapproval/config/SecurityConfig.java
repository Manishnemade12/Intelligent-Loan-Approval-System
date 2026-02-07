package com.loanapproval.config;

import com.loanapproval.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        // Application endpoints
                        .requestMatchers(HttpMethod.POST, "/api/applications").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/applications/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/applications/**").hasAnyRole("OFFICER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/applications/*/approve").hasAnyRole("OFFICER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/applications/*/reject").hasAnyRole("OFFICER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/applications/*/review-request").hasAnyRole("OFFICER", "ADMIN")
                        
                        // Document endpoints
                        .requestMatchers(HttpMethod.POST, "/api/documents/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/documents/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/documents/*/verify").hasAnyRole("OFFICER", "ADMIN")
                        
                        // Dashboard endpoints
                        .requestMatchers("/api/dashboard/**").authenticated()
                        
                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // Deny all other requests
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
