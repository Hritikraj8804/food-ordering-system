package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dto.MenuItemDto;
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
        
        if (image != null && !image.isEmpty()) {
            saveImage(image, savedItem.getId());
        }
        
        return savedItem;
    }
    
    public List<MenuItemDto> getAvailableMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndAvailableTrue(restaurantId).stream()
            .map(item -> new MenuItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getType(),
                item.getAvailable(),
                item.getRestaurant().getId(),
                item.getRestaurant().getName()
            ))
            .collect(Collectors.toList());
    }
    
    public List<MenuItemDto> getAllMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId).stream()
            .map(item -> new MenuItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getType(),
                item.getAvailable(),
                item.getRestaurant().getId(),
                item.getRestaurant().getName()
            ))
            .collect(Collectors.toList());
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
        
        Review review = new Review();
        review.setOrder(order);
        review.setUser(user);
        review.setRestaurant(order.getRestaurant());
        review.setRating(rating);
        review.setComment(comment);
        
        return reviewRepository.save(review);
    }
    
    public List<Review> getRestaurantReviews(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }
    
    public List<Review> getItemReviews(Long itemId) {
        return reviewRepository.findByMenuItemId(itemId);
    }
}