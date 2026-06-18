package gde.gde_website.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Allow requests for /games endpoints without authentication
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/games").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/games/**").permitAll()
                        // #TODO: debug mode
                        .anyRequest().permitAll()
                )
                // Disable Basic Auth for REST API
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // Disable CSRF, or POST/PUT/PATCH requests would be blocked
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}