package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI foodOrderingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🍕 Food Ordering System API")
                        .description("A comprehensive Spring Boot application for online food ordering with role-based access control")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Food Ordering System")
                                .email("support@foodordering.com")));
    }
}