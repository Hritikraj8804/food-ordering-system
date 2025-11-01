package com.foodapp.food_ordering_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.foodapp.food_ordering_system", "controller", "service", "repository", "aspect", "exception", "config", "security", "dto"})
@EnableJpaRepositories(basePackages = "repository")
@EntityScan(basePackages = "entity")
@EnableAspectJAutoProxy
public class FoodOrderingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodOrderingSystemApplication.class, args);
	}

}
