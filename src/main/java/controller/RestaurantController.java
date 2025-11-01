package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import entity.Restaurant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurant Management", description = "APIs for managing restaurants (HOTEL role required for creation)")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Dependency Injection: Spring automatically provides the RestaurantService instance
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Operation(
        summary = "Create a new restaurant",
        description = "Creates a new restaurant. Only users with HOTEL role can create restaurants."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Restaurant created successfully",
            content = @Content(schema = @Schema(implementation = Restaurant.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only HOTEL users can create restaurants",
            content = @Content),
        @ApiResponse(responseCode = "404", description = "Hotel owner not found",
            content = @Content)
    })
    @PostMapping("/{hotelOwnerId}")
    public ResponseEntity<Restaurant> addRestaurant(
            @Parameter(description = "ID of the hotel owner (must have HOTEL role)", required = true, example = "2")
            @PathVariable Long hotelOwnerId, 
            @Parameter(description = "Restaurant details", required = true)
            @Valid @RequestBody Restaurant restaurant) {
        
        // The @Valid annotation triggers validation checks defined on the Restaurant entity fields.
        Restaurant newRestaurant = restaurantService.addRestaurant(restaurant, hotelOwnerId);
        
        // Return 201 Created status
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get all restaurants",
        description = "Retrieves a list of all registered restaurants. Accessible to all users."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully",
            content = @Content(schema = @Schema(implementation = Restaurant.class)))
    })
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        
        // Return 200 OK status
        return ResponseEntity.ok(restaurants);
    }
}