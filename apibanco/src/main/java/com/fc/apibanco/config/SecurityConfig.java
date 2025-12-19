package com.fc.apibanco.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fc.apibanco.security.ApiKeyFilter;
import com.fc.apibanco.security.JwtAuthenticationFilter;
import com.fc.apibanco.service.CustomUserDetailsService;
import com.fc.apibanco.util.Constantes;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          ApiKeyFilter apiKeyFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/descargar/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/registro").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/subir/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/subir-multiple/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()

                // API Keys: ADMIN solo lectura, SUPERADMIN todo
                .requestMatchers(HttpMethod.GET, "/api/apikeys/**")
                    .hasAnyRole(Constantes.ADMIN, Constantes.SUPERADMIN)
                .requestMatchers(HttpMethod.POST, "/api/apikeys/**")
                    .hasRole(Constantes.SUPERADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/apikeys/**")
                    .hasRole(Constantes.SUPERADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/apikeys/**")
                    .hasRole(Constantes.SUPERADMIN)

                // Usuarios: ADMIN solo lectura, SUPERADMIN todo
                .requestMatchers(HttpMethod.GET, "/api/usuarios/**")
                    .hasAnyRole(Constantes.ADMIN, Constantes.SUPERADMIN)
                .requestMatchers(HttpMethod.POST, "/api/usuarios/**")
                    .hasRole(Constantes.SUPERADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**")
                    .hasRole(Constantes.SUPERADMIN)
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**")
                    .hasRole(Constantes.SUPERADMIN)

                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

