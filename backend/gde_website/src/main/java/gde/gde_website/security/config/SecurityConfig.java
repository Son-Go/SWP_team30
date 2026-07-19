package gde.gde_website.security.config;

import gde.gde_website.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This is a class which controlling HTTP requests security
 * @Author: Artemii Gorelov
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    /**
     * This method represents chain of security rules for HTTP requests
     * @param http - HTTP security method
     * @return filter of rules decided to apply for specific requests
     * @throws Exception if some request which is linked with authentication is denied
     *
     * @Author: Artemii Gorelov
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Registration and login are allowed for all users
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // Allow requests for GET /games endpoints without authentication
                        .requestMatchers(HttpMethod.GET, "/games").permitAll()
                        .requestMatchers(HttpMethod.GET, "/games/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/games/author/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/{id}/games").permitAll()

                        // Endpoints which requires JWT token authentication
                        .requestMatchers(HttpMethod.POST, "/games").authenticated()
                        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/games/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/games/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/users/me/password").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/games/{game_id}/comments").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/games/{game_id}/comments/{comment_id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/games/{game_id}/comments/{comment_id}").authenticated()

                        // Admin endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/games/**").hasRole("ADMIN")

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
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Unauthorized\", \"message\": \"" +
                                            authException.getMessage() + "\"}"
                            );
                        })))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * This method is used for encode user password
     * @return encoded BCrypt user password
     * @Author: Artemii Gorelov
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}