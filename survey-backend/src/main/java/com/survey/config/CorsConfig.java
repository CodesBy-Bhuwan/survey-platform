
package com.survey.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // Read the allowed frontend origin from application.properties.
    @Value("${app.cors.allowed-origin:http://localhost:5174}")
    private String allowedOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // Allow the React frontend to communicate with backend APIs.
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
                .allowedHeaders("*");
    }
}
