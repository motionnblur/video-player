package com.server.demo.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET","POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowCredentials(true);
        //.allowedMethods("GET", "POST", "PUT", "DELETE") // Specify allowed HTTP methods
        //.maxAge(3600); // Set preflight response cache duration
    }
}
