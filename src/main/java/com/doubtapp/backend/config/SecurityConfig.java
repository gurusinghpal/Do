package com.doubtapp.backend.config;

import com.doubtapp.backend.service.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JpaUserDetailsService jpaUserDetailsService;

    public SecurityConfig(JpaUserDetailsService jpaUserDetailsService) {
        this.jpaUserDetailsService = jpaUserDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Publicly accessible pages and static resources
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()

                        // Admin-only endpoints
                        .requestMatchers("/api/test/**").hasRole("ADMIN")

                        // Student-only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/doubts/post").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/api/doubts/student/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/doubts/student/**").hasRole("STUDENT")

                        // Mentor-only endpoints
                        .requestMatchers(HttpMethod.POST, "/api/doubts/answer/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/doubts/mentor/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/doubts/mentor/**").hasRole("MENTOR")

                        // Publicly accessible GET endpoints for doubts
                        .requestMatchers(HttpMethod.GET, "/api/doubts/**").permitAll()

                        // Any other request requires authentication
                        .anyRequest().authenticated()
                )
                .userDetailsService(jpaUserDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
