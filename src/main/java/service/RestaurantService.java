package service;

import java.util.List;

import org.springframework.stereotype.Service;

import entity.Restaurant;
import entity.Role;
import entity.User;
import exception.UnauthorizedActionException;
import repository.RestaurantRepository;

@Service
public class RestaurantService {

 private final RestaurantRepository restaurantRepository;
 private final UserService userService; 

 public RestaurantService(RestaurantRepository restaurantRepository, UserService userService) {
     this.restaurantRepository = restaurantRepository;
     this.userService = userService;
 }

 /**
  * Enforces the HOTEL role before creating a restaurant.
  */
 public Restaurant addRestaurant(Restaurant restaurant, Long hotelOwnerId) {
     User hotelOwner = userService.getUserById(hotelOwnerId); 

     // *** ROLE CHECK: Throw 403 Forbidden if not a HOTEL ***
     if (hotelOwner.getRole() != Role.HOTEL) {
         throw new UnauthorizedActionException("Only users with the HOTEL role can add a restaurant.");
     }

     restaurant.setHotelOwner(hotelOwner);
     return restaurantRepository.save(restaurant);
 }

 public List<Restaurant> getAllRestaurants() {
     return restaurantRepository.findAll();
 }
}