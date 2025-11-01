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

import entity.User;
import jakarta.validation.Valid;
import service.UserService;

@RestController // 1. Defines this class to handle REST requests
@RequestMapping("/api/users") // Base path for all user operations
public class UserController {

    private final UserService userService;

    // Dependency Injection: Injecting the service that holds the business logic
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/users/register
     * Handles the creation of a new user. 
     * The service layer handles default role assignment and email uniqueness check.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        
        // @Valid ensures input data conforms to constraints (e.g., @NotNull)
        User registeredUser = userService.registerUser(user);
        
        // Return 201 Created status
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * GET /api/users
     * Retrieves all users. Used for login functionality.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id}
     * Retrieves a user by their ID. Useful for authentication/verification.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        // The service throws ResourceNotFoundException (handled by GlobalExceptionHandler)
        User user = userService.getUserById(id);
        
        // Return 200 OK status
        return ResponseEntity.ok(user);
    }
}
