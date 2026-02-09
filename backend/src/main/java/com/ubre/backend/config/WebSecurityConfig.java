package com.ubre.backend.config;

import java.util.Arrays;

import com.ubre.backend.security.RestAuthenticationEntryPoint;
import com.ubre.backend.security.TokenAuthenticationFilter;
import com.ubre.backend.service.impl.CustomUserDetailsService;
import com.ubre.backend.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Definisemo prava pristupa za zahteve ka odredjenim URL-ovima/rutama
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());

        // JWT + STATELESS -> CSRF OFF (inače dobijaš 403 na POST/PUT/PATCH/DELETE)
        http.csrf(csrf -> csrf.disable());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(restAuthenticationEntryPoint));

        http.authorizeHttpRequests(request -> request
            // CORS preflight
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            .requestMatchers("/api/auth/login",
                                            "/api/users/register",
                                            "/api/users/{id}/avatar",
                                            "/api/auth/activate",
                            "/api/auth/forgot-password", 
                             "/api/auth/reset-password").permitAll()
            .requestMatchers("/error").permitAll()
            .requestMatchers(
                "/",
                "/index.html",
                "/favicon.ico",
                "/ws/**"
            ).permitAll()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .anyRequest().authenticated()
        );

        http.addFilterBefore(
            new TokenAuthenticationFilter(tokenUtils, userDetailsService()),
            UsernamePasswordAuthenticationFilter.class
        );

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    // Podesavanja CORS-a
    // https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "https://ubre.notixdms.com"));
        configuration.setAllowedMethods(Arrays.asList("POST", "PUT", "GET", "OPTIONS", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // OVO rešava SockJS/CORS "credentials include" problem
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
