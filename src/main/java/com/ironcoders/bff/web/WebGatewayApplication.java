package com.ironcoders.bff.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * BFF Web Gateway Application
 * 
 * API Gateway específico para clientes Web (Provider/Admin)
 * 
 * Puerto: 8081
 * Clientes: Aplicación Web (Proveedores y Administradores)
 * Backend: http://localhost:8080 (AquaConecta Monolito)
 * 
 * Características:
 * - Acceso completo a todos los bounded contexts
 * - Circuit breakers para resiliencia
 * - Autenticación Auth0 (JWT)
 * - Permisos: ROLE_PROVIDER, ROLE_ADMIN
 */
@SpringBootApplication
public class WebGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebGatewayApplication.class, args);
    }
}

/**
 * Fallback Controller para Circuit Breaker
 */
@RestController
class FallbackController {
    
    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        return Mono.just("{\"error\": \"Service temporarily unavailable. Please try again later.\", \"status\": 503}");
    }
}
