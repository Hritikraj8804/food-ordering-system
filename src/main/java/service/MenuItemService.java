package service;

import java.util.List;
import org.springframework.stereotype.Service;
import entity.MenuItem;
import entity.Restaurant;
import entity.Role;
import entity.User;
import exception.UnauthorizedActionException;
import repository.MenuItemRepository;
import repository.RestaurantRepository;
import repository.UserRepository;

@Service
public class MenuItemService {
    
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    
    public MenuItemService(MenuItemRepository menuItemRepository, 
                          RestaurantRepository restaurantRepository,
                          UserRepository userRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }
    
    public MenuItem addMenuItem(MenuItem menuItem, Long restaurantId, Long ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Owner not found"));
            
        if (owner.getRole() != Role.HOTEL) {
            throw new UnauthorizedActionException("Only HOTEL users can add menu items");
        }
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            
        if (!restaurant.getHotelOwner().getId().equals(ownerId)) {
            throw new UnauthorizedActionException("You can only add items to your own restaurant");
        }
        
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }
    
    public List<MenuItem> getAvailableMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId);
    }
}