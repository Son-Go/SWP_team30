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
                        // Разрешаем GET запросы к /games всем без исключения
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/games").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/games/**").permitAll()

                        // Временно разрешаем всё остальное, чтобы не мешало разрабатывать
                        // Потом заменишь на .anyRequest().authenticated()
                        .anyRequest().permitAll()
                )
                // Отключаем стандартную форму логина и Basic Auth для REST API
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // Важно для REST: отключаем CSRF, иначе POST/PUT/PATCH запросы будут блокироваться
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}