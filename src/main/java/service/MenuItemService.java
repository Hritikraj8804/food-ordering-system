package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import entity.MenuItem;
import entity.Order;
import entity.Restaurant;
import entity.Review;
import entity.Role;
import entity.User;
import exception.ResourceNotFoundException;
import exception.UnauthorizedActionException;
import repository.MenuItemRepository;
import repository.OrderRepository;
import repository.RestaurantRepository;
import repository.ReviewRepository;
import repository.UserRepository;

@Service
public class MenuItemService {
    
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    
    public MenuItemService(MenuItemRepository menuItemRepository, 
                          RestaurantRepository restaurantRepository,
                          UserRepository userRepository,
                          ReviewRepository reviewRepository,
                          OrderRepository orderRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }
    
    public MenuItem addMenuItem(MenuItem menuItem, Long restaurantId, Long ownerId, MultipartFile image) {
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
        MenuItem savedItem = menuItemRepository.save(menuItem);
        
        // Save image with item ID as filename
        if (image != null && !image.isEmpty()) {
            saveImage(image, savedItem.getId());
        }
        
        return savedItem;
    }
    
    public List<MenuItem> getAvailableMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId);
    }
    
    public List<MenuItem> getAllMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }
    
    public MenuItem updateMenuItem(Long itemId, MenuItem updatedItem, Long ownerId, MultipartFile image) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
            
        if (owner.getRole() != Role.HOTEL) {
            throw new UnauthorizedActionException("Only HOTEL users can update menu items");
        }
        
        MenuItem existingItem = menuItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
            
        if (!existingItem.getRestaurant().getHotelOwner().getId().equals(ownerId)) {
            throw new UnauthorizedActionException("You can only update your own menu items");
        }
        
        existingItem.setName(updatedItem.getName());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setType(updatedItem.getType());
        if (updatedItem.getAvailable() != null) {
            existingItem.setAvailable(updatedItem.getAvailable());
        }
        
        // Update image if provided
        if (image != null && !image.isEmpty()) {
            saveImage(image, itemId);
        }
        
        return menuItemRepository.save(existingItem);
    }
    
    public void deleteMenuItem(Long itemId, Long ownerId) {
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
            
        if (owner.getRole() != Role.HOTEL) {
            throw new UnauthorizedActionException("Only HOTEL users can delete menu items");
        }
        
        MenuItem item = menuItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
            
        if (!item.getRestaurant().getHotelOwner().getId().equals(ownerId)) {
            throw new UnauthorizedActionException("You can only delete your own menu items");
        }
        
        menuItemRepository.delete(item);
        
        // Delete associated image file
        deleteImage(itemId);
    }
    
    private void saveImage(MultipartFile image, Long itemId) {
        try {
            Path uploadDir = Paths.get("uploads/menu-images");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            String extension = getFileExtension(image.getOriginalFilename());
            Path imagePath = uploadDir.resolve(itemId + extension);
            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
    }
    
    private void deleteImage(Long itemId) {
        try {
            Path jpgPath = Paths.get("uploads/menu-images/" + itemId + ".jpg");
            Path pngPath = Paths.get("uploads/menu-images/" + itemId + ".png");
            Files.deleteIfExists(jpgPath);
            Files.deleteIfExists(pngPath);
        } catch (IOException e) {
            // Ignore deletion errors
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return ".jpg";
    }
    
    public Review addReview(Long orderId, Long userId, Integer rating, String comment) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        if (!order.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You can only review your own orders");
        }
        
        // Check if review already exists
        if (reviewRepository.findByOrderIdAndUserId(orderId, userId).isPresent()) {
            throw new UnauthorizedActionException("You have already reviewed this order");
        }
        
        Review review = new Review();
        review.setOrder(order);
        review.setUser(user);
        review.setRestaurant(order.getRestaurant());
        review.setRating(rating);
        review.setComment(comment);
        
        return reviewRepository.save(review);
    }
    
    public List<Review> getOrderReviews(Long orderId) {
        return reviewRepository.findByOrderId(orderId);
    }
    
    public List<Review> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }
    
    public List<Review> getItemReviews(Long itemId) {
        return reviewRepository.findByMenuItemId(itemId);
    }
}