package com.politicabr.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins; // Verifique se este valor no arquivo de propriedades não está incorretamente definido.

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Você está corretamente usando allowedOriginPatterns
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:5000") // Verifique se é uma origem correta
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);
    }
}