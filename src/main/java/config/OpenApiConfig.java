package config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Swiggy Clone Food Ordering API",
        version = "1.0",
        description = "A Spring Boot mini-project demonstrating core syllabus concepts."
    )
)
public class OpenApiConfig {
    // Configuration handled by annotations
}