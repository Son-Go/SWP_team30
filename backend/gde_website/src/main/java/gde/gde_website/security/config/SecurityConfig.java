package gde.gde_website.security.config;

import gde.gde_website.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Registration and login are allowed for all users
                        .requestMatchers("/auth/register", "/auth/login").permitAll()

                        // Allow requests for GET /games endpoints without authentication
                        .requestMatchers(HttpMethod.GET, "/games").permitAll()
                        .requestMatchers(HttpMethod.GET, "/games/**").permitAll()

                        // Endpoints which requires JWT token authentication
                        .requestMatchers(HttpMethod.POST, "/games/**").authenticated()
                        .requestMatchers("/auth/me").authenticated()

                        // Debug endpoints

                        // All other endpoints
                        .anyRequest().denyAll()
                )
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Disable Basic Auth for REST API
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // Disable CSRF, or POST/PUT/PATCH requests would be blocked
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}