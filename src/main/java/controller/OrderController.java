package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.OrderRequestDto;
import entity.Order;
import entity.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import service.OrderService;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for placing and managing food orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@Operation(
	    summary = "Place a new order",
	    description = "Places a new food order. Only USER role can place orders. Triggers AOP logging and uses @Transactional."
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "201", description = "Order placed successfully",
	        content = @Content(schema = @Schema(implementation = Order.class))),
	    @ApiResponse(responseCode = "403", description = "Forbidden - Only USER role can place orders",
	        content = @Content),
	    @ApiResponse(responseCode = "404", description = "User or restaurant not found",
	        content = @Content)
	})
	@PostMapping("/{userId}/{restaurantId}")
	public ResponseEntity<Order> placeOrder(
	    @Parameter(description = "Customer ID (must have USER role)", required = true, example = "1")
	    @PathVariable Long userId, 
	    @Parameter(description = "Restaurant ID", required = true, example = "1")
	    @PathVariable Long restaurantId, 
	    @Parameter(description = "Order details with menu items and quantities", required = true)
	    @Valid @RequestBody OrderRequestDto orderRequestDto)
	{
	    // Pass the DTO to the service
	    Order newOrder = orderService.placeOrder(orderRequestDto, userId, restaurantId); 
	    return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
	}

	@Operation(
	    summary = "Get user orders",
	    description = "Retrieves all orders placed by a specific user"
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
	        content = @Content(schema = @Schema(implementation = Order.class)))
	})
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Order>> getOrdersByUser(
	    @Parameter(description = "User ID", required = true, example = "1")
	    @PathVariable Long userId) {
		return ResponseEntity.ok(orderService.getOrdersByUser(userId));
	}

	@Operation(
	    summary = "Update order status",
	    description = "Updates the status of an order. Only restaurant owners (HOTEL role) can update orders for their restaurants."
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Order status updated successfully",
	        content = @Content(schema = @Schema(implementation = Order.class))),
	    @ApiResponse(responseCode = "403", description = "Forbidden - Only restaurant owner can update order status",
	        content = @Content),
	    @ApiResponse(responseCode = "404", description = "Order not found",
	        content = @Content)
	})
	@PutMapping("/{orderId}/status")
	public ResponseEntity<Order> updateOrderStatus(
	    @Parameter(description = "Order ID", required = true, example = "1")
	    @PathVariable Long orderId, 
	    @Parameter(description = "New order status", required = true, example = "PREPARING")
	    @RequestParam OrderStatus status,
	    @Parameter(description = "Updater ID (must be restaurant owner)", required = true, example = "2")
	    @RequestParam Long updaterId) {
		Order updatedOrder = orderService.updateOrderStatus(orderId, status, updaterId);
		return ResponseEntity.ok(updatedOrder);
	}

	@Operation(
	    summary = "Get restaurant orders",
	    description = "Retrieves all orders for a specific restaurant"
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Restaurant orders retrieved successfully",
	        content = @Content(schema = @Schema(implementation = Order.class)))
	})
	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<List<Order>> getOrdersByRestaurant(
	    @Parameter(description = "Restaurant ID", required = true, example = "1")
	    @PathVariable Long restaurantId) {
		return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
	}
}