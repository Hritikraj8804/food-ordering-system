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
import jakarta.validation.Valid;
import service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	// POST: /api/orders/{userId}/{restaurantId}
	// This method is the AOP Pointcut and the @Transactional starter.
	@PostMapping("/{userId}/{restaurantId}")
	public ResponseEntity<Order> placeOrder(
	    @PathVariable Long userId, 
	    @PathVariable Long restaurantId, 
	    @Valid @RequestBody OrderRequestDto orderRequestDto) // <--- Changed!
	{
	    // Pass the DTO to the service
	    Order newOrder = orderService.placeOrder(orderRequestDto, userId, restaurantId); 
	    return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
	}

	// GET: /api/orders/user/{userId}
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable Long userId) {
		return ResponseEntity.ok(orderService.getOrdersByUser(userId));
	}

	// PUT: /api/orders/{orderId}/status?status=PREPARING&updaterId=2
	// Requires HOTEL role and ownership check in the Service layer.
	@PutMapping("/{orderId}/status")
	public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status,
			@RequestParam Long updaterId) {
		Order updatedOrder = orderService.updateOrderStatus(orderId, status, updaterId);
		return ResponseEntity.ok(updatedOrder);
	}

	// GET: /api/orders/restaurant/{restaurantId}
	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<List<Order>> getOrdersByRestaurant(@PathVariable Long restaurantId) {
		return ResponseEntity.ok(orderService.getOrdersByRestaurant(restaurantId));
	}
}