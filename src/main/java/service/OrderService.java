package service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.OrderRequestDto;
import entity.Order;
import entity.OrderItem;
import entity.OrderStatus;
import entity.Restaurant;
import entity.Role;
import entity.User;
import exception.ResourceNotFoundException;
import exception.UnauthorizedActionException;
import repository.OrderRepository;
import repository.RestaurantRepository;

@Service
public class OrderService {

 private final OrderRepository orderRepository;
 private final UserService userService;
 private final RestaurantRepository restaurantRepository;

 public OrderService(OrderRepository orderRepository, UserService userService, RestaurantRepository restaurantRepository) {
     this.orderRepository = orderRepository;
     this.userService = userService;
     this.restaurantRepository = restaurantRepository;
 }

 /**
  * Places a new order. MUST be atomic!
  */
 @Transactional
 public Order placeOrder(OrderRequestDto orderRequestDto, Long userId, Long restaurantId) {
     // Validate user exists and has USER role
     User customer = userService.getUserById(userId);
     if (customer.getRole() != Role.USER) {
         throw new UnauthorizedActionException("Only users with USER role can place orders.");
     }
     
     // Validate restaurant exists
     Restaurant restaurant = restaurantRepository.findById(restaurantId)
             .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found."));
     
     Order newOrder = new Order();
     newOrder.setUser(customer);
     newOrder.setRestaurant(restaurant);
     
     // Process items from the DTO
     double total = 0.0;
     for (OrderItem item : orderRequestDto.getItems()) {
         OrderItem orderItem = new OrderItem();
         orderItem.setItemName(item.getItemName());
         orderItem.setPrice(item.getPrice());
         orderItem.setQuantity(item.getQuantity());
         newOrder.addItem(orderItem);
         total += item.getPrice() * item.getQuantity();
     }

     newOrder.setTotalAmount(total);
     
     return orderRepository.save(newOrder); 
 }

 /**
  * Updates order status. Requires HOTEL ownership check.
  */
 public Order updateOrderStatus(Long orderId, OrderStatus newStatus, Long updaterId) {
     Order order = orderRepository.findById(orderId)
             .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
     
     User updater = userService.getUserById(updaterId);

     // Role Check: Must be a HOTEL AND must own the restaurant associated with the order
     if (updater.getRole() != Role.HOTEL || !order.getRestaurant().getHotelOwner().getId().equals(updaterId)) {
         throw new UnauthorizedActionException("You are not authorized to update this order.");
     }

     order.setStatus(newStatus);
     return orderRepository.save(order);
 }
 
 public List<Order> getOrdersByUser(Long userId) {
     return orderRepository.findByUserId(userId);
 }
}
