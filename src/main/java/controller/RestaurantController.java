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
import jakarta.validation.Valid;
import service.RestaurantService;

@RestController // Marks this class as a REST controller
@RequestMapping("/api/restaurants") // Base URL for all methods in this class
public class RestaurantController {

    private final RestaurantService restaurantService;

    // Dependency Injection: Spring automatically provides the RestaurantService instance
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    /**
     * POST /api/restaurants/{hotelOwnerId}
     * Adds a new restaurant. Requires HOTEL role (check performed in Service).
     * @param hotelOwnerId The ID of the user attempting to register the restaurant.
     */
    @PostMapping("/{hotelOwnerId}")
    public ResponseEntity<Restaurant> addRestaurant(
            @PathVariable Long hotelOwnerId, 
            @Valid @RequestBody Restaurant restaurant) {
        
        // The @Valid annotation triggers validation checks defined on the Restaurant entity fields.
        Restaurant newRestaurant = restaurantService.addRestaurant(restaurant, hotelOwnerId);
        
        // Return 201 Created status
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    /**
     * GET /api/restaurants
     * Retrieves all registered restaurants. Accessible to all users.
     */
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        
        // Return 200 OK status
        return ResponseEntity.ok(restaurants);
    }
}