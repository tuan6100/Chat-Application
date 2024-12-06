package com.chat.app.config;

import com.chat.app.security.JwtTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class SecurityConfig {

    private final JwtTokenValidator jwtTokenValidator;


    public SecurityConfig(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtTokenValidator, UsernamePasswordAuthenticationFilter.class);
        http.authorizeHttpRequests( (req) -> req
                .requestMatchers("/api/auth/**","/error").permitAll()
                .anyRequest().authenticated()
        );
        http.logout((logout) -> logout.logoutSuccessUrl("/api/auth/logout"));
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(CsrfConfigurer::disable);
        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
//        config.setAllowedOrigins(List.of("http://localhost:3000", "https://2b59-42-113-16-145.ngrok-free.app/"));
        config.addAllowedOriginPattern("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "X-Refresh-Token",
                "Content-Type",
                "Cache-Control",
                "accept",
                "user-agent",
                "x-requested-with"
        ));
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Refresh-Token"
        ));
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}