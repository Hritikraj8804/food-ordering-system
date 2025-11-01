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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import service.UserService;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for user registration and authentication")
public class UserController {

    private final UserService userService;

    // Dependency Injection: Injecting the service that holds the business logic
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with either USER (customer) or HOTEL (restaurant owner) role"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists",
            content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(
        @Parameter(description = "User registration details", required = true)
        @Valid @RequestBody User user) {
        
        // @Valid ensures input data conforms to constraints (e.g., @NotNull)
        User registeredUser = userService.registerUser(user);
        
        // Return 201 Created status
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get all users",
        description = "Retrieves a list of all registered users (used for login functionality)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = User.class)))
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a specific user by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
            content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
            content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
        @Parameter(description = "User ID", required = true, example = "1")
        @PathVariable Long id) {
        // The service throws ResourceNotFoundException (handled by GlobalExceptionHandler)
        User user = userService.getUserById(id);
        
        // Return 200 OK status
        return ResponseEntity.ok(user);
    }
}
