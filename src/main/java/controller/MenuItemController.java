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
            @Valid @RequestBody MenuItem menuItem) {
        MenuItem newItem = menuItemService.addMenuItem(menuItem, restaurantId, ownerId);
        return new ResponseEntity<>(newItem, HttpStatus.CREATED);
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getMenuItems(@PathVariable Long restaurantId) {
        List<MenuItem> items = menuItemService.getAvailableMenuItems(restaurantId);
        return ResponseEntity.ok(items);
    }
}