package com.ironcoders.bff.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration para BFF Web Gateway
 * 
 * Configuracion OAuth2 JWT (Auth0) + CORS
 * 
 * IMPORTANTE: CORS debe configurarse aqui (no en application.yml)
 * porque Spring Security procesa requests antes que Gateway
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable()) // Deshabilitamos CORS de Security, usamos CorsWebFilter
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CRITICO: Permitir OPTIONS (CORS preflight)
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/fallback").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})
            )
            .build();
    }

    /**
     * CORS Filter - Se ejecuta ANTES de Spring Security
     * Esto permite que las peticiones OPTIONS (preflight) pasen sin autenticacion
     * 
     * @Order(Ordered.HIGHEST_PRECEDENCE) asegura que este filtro se ejecute primero
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Origenes permitidos - USAR setAllowedOriginPatterns en lugar de setAllowedOrigins
        // para que funcione correctamente con credentials
        corsConfig.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:4200",
            "http://localhost:3000",
            "http://localhost:*",  // Permitir cualquier puerto localhost
            "https://aquaconecta-ase.netlify.app",
            "https://cool-pasca-e1a790.netlify.app"
        ));
        
        // Metodos HTTP permitidos
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        
        // Headers permitidos
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        
        // Headers expuestos (que el frontend puede leer)
        corsConfig.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type",
            "X-Client-Type",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
        ));
        
        // Permitir credenciales (cookies, Authorization header)
        corsConfig.setAllowCredentials(true);
        
        // Cachear preflight por 1 hora
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
