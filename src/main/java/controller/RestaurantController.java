package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.RestaurantDto;
import entity.Restaurant;
import jakarta.validation.Valid;
import service.RestaurantService;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin(origins = "http://localhost:3000")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/{hotelOwnerId}")
    public ResponseEntity<Restaurant> addRestaurant(
            @PathVariable Long hotelOwnerId, 
            @Valid @RequestBody Restaurant restaurant) {
        
        Restaurant newRestaurant = restaurantService.addRestaurant(restaurant, hotelOwnerId);
        return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        List<RestaurantDto> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }
}