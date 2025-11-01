package service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import dto.RestaurantDto;
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

    public Restaurant addRestaurant(Restaurant restaurant, Long hotelOwnerId) {
        User hotelOwner = userService.getUserById(hotelOwnerId); 

        if (hotelOwner.getRole() != Role.HOTEL) {
            throw new UnauthorizedActionException("Only users with the HOTEL role can add a restaurant.");
        }

        restaurant.setHotelOwner(hotelOwner);
        return restaurantRepository.save(restaurant);
    }

    public List<RestaurantDto> getAllRestaurants() {
        return restaurantRepository.findAll().stream()
            .map(restaurant -> new RestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCuisineType(),
                restaurant.getHotelOwner().getId(),
                restaurant.getHotelOwner().getName()
            ))
            .collect(Collectors.toList());
    }
}