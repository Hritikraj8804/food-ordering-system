package controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dto.MenuItemDto;
import dto.ReviewDto;
import entity.MenuItem;
import entity.Review;
import service.MenuItemService;

@RestController
@RequestMapping("/api/menu-items")
@CrossOrigin(origins = "http://localhost:3000")
public class MenuItemController {
    
    private final MenuItemService menuItemService;
    
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }
    
    @PostMapping("/restaurant/{restaurantId}/owner/{ownerId}")
    public ResponseEntity<MenuItem> addMenuItem(
            @PathVariable Long restaurantId,
            @PathVariable Long ownerId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("type") String type,
            @RequestParam("image") MultipartFile image) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setType(type);
        
        MenuItem newItem = menuItemService.addMenuItem(menuItem, restaurantId, ownerId, image);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemDto>> getMenuItems(@PathVariable Long restaurantId) {
        List<MenuItemDto> items = menuItemService.getAvailableMenuItems(restaurantId);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/restaurant/{restaurantId}/all")
    public ResponseEntity<List<MenuItemDto>> getAllMenuItems(@PathVariable Long restaurantId) {
        List<MenuItemDto> items = menuItemService.getAllMenuItems(restaurantId);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/image/{itemId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long itemId) {
        try {
            Path imagePath = Paths.get("uploads/menu-images/" + itemId + ".jpg");
            if (!Files.exists(imagePath)) {
                imagePath = Paths.get("uploads/menu-images/" + itemId + ".png");
            }
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(imagePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/review/{orderId}/user/{userId}")
    public ResponseEntity<Review> addReview(
            @PathVariable Long orderId,
            @PathVariable Long userId,
            @RequestParam Integer rating,
            @RequestParam String comment) {
        Review review = menuItemService.addReview(orderId, userId, rating, comment);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }
    
    @GetMapping("/reviews/restaurant/{restaurantId}")
    public ResponseEntity<List<ReviewDto>> getRestaurantReviews(@PathVariable Long restaurantId) {
        List<ReviewDto> reviews = menuItemService.getRestaurantReviewsDto(restaurantId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/reviews/item/{itemId}")
    public ResponseEntity<List<ReviewDto>> getItemReviews(@PathVariable Long itemId) {
        List<ReviewDto> reviews = menuItemService.getItemReviewsDto(itemId);
        return ResponseEntity.ok(reviews);
    }
}