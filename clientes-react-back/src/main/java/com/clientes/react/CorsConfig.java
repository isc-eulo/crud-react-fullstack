package com.clientes.react;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class CorsConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Origen de tu React
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // <--- PATCH DEBE ESTAR AQUÍ
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}