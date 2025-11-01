package controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import entity.Review;
import entity.MenuItem;
import jakarta.validation.Valid;
import service.MenuItemService;

@RestController
@RequestMapping("/api/menu-items")
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
    public ResponseEntity<List<MenuItem>> getMenuItems(@PathVariable Long restaurantId) {
        List<MenuItem> items = menuItemService.getAvailableMenuItems(restaurantId);
        return ResponseEntity.ok(items);
    }
    
    @GetMapping("/restaurant/{restaurantId}/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems(@PathVariable Long restaurantId) {
        List<MenuItem> items = menuItemService.getAllMenuItems(restaurantId);
        return ResponseEntity.ok(items);
    }
    
    @PutMapping("/{itemId}/owner/{ownerId}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long itemId,
            @PathVariable Long ownerId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("type") String type,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setType(type);
        
        MenuItem updatedItem = menuItemService.updateMenuItem(itemId, menuItem, ownerId, image);
        return ResponseEntity.ok(updatedItem);
    }
    
    @DeleteMapping("/{itemId}/owner/{ownerId}")
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable Long itemId,
            @PathVariable Long ownerId) {
        menuItemService.deleteMenuItem(itemId, ownerId);
        return ResponseEntity.noContent().build();
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
    
    @GetMapping("/reviews/{orderId}")
    public ResponseEntity<List<Review>> getOrderReviews(@PathVariable Long orderId) {
        List<Review> reviews = menuItemService.getOrderReviews(orderId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/reviews/restaurant/{restaurantId}")
    public ResponseEntity<List<Review>> getRestaurantReviews(@PathVariable Long restaurantId) {
        List<Review> reviews = menuItemService.getRestaurantReviews(restaurantId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/reviews/item/{itemId}")
    public ResponseEntity<List<Review>> getItemReviews(@PathVariable Long itemId) {
        List<Review> reviews = menuItemService.getItemReviews(itemId);
        return ResponseEntity.ok(reviews);
    }
}