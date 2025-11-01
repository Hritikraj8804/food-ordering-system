package service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dto.OrderRequestDto;
import entity.MenuItem;
import entity.Order;
import entity.OrderItem;
import entity.OrderStatus;
import entity.Restaurant;
import entity.Role;
import entity.User;
import exception.ResourceNotFoundException;
import exception.UnauthorizedActionException;
import repository.MenuItemRepository;
import repository.OrderRepository;
import repository.RestaurantRepository;

@Service
public class OrderService {

 private final OrderRepository orderRepository;
 private final UserService userService;
 private final RestaurantRepository restaurantRepository;
 private final MenuItemRepository menuItemRepository;

 public OrderService(OrderRepository orderRepository, UserService userService, 
                    RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository) {
     this.orderRepository = orderRepository;
     this.userService = userService;
     this.restaurantRepository = restaurantRepository;
     this.menuItemRepository = menuItemRepository;
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
     for (OrderRequestDto.OrderItemDto itemDto : orderRequestDto.getItems()) {
         MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
             .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
             
         OrderItem orderItem = new OrderItem();
         orderItem.setMenuItem(menuItem);
         orderItem.setQuantity(itemDto.getQuantity());
         orderItem.setPriceAtOrder(menuItem.getPrice()); // Lock in current price
         orderItem.setOrder(newOrder);
         newOrder.addItem(orderItem);
         total += menuItem.getPrice() * itemDto.getQuantity();
     }

     newOrder.setTotalAmount(total);
     
     return orderRepository.save(newOrder); 
 }

 /**
  * Updates order status with role-based authorization.
  */
 public Order updateOrderStatus(Long orderId, OrderStatus newStatus, Long updaterId) {
     Order order = orderRepository.findById(orderId)
             .orElseThrow(() -> new ResourceNotFoundException("Order not found."));
     
     User updater = userService.getUserById(updaterId);

     // Authorization based on status and role
     if (newStatus == OrderStatus.DELIVERED) {
         // Debug logging
         System.out.println("Attempting to mark order as delivered:");
         System.out.println("Order ID: " + orderId);
         System.out.println("Order User ID: " + order.getUser().getId());
         System.out.println("Updater ID: " + updaterId);
         System.out.println("Updater Role: " + updater.getRole());
         System.out.println("Current Order Status: " + order.getStatus());
         
         // Only the customer who placed the order can mark it as delivered
         if (updater.getRole() != Role.USER || !order.getUser().getId().equals(updaterId)) {
             throw new UnauthorizedActionException("Only the customer can mark the order as delivered.");
         }
         // Order must be OUT_FOR_DELIVERY to be marked as delivered
         if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
             throw new UnauthorizedActionException("Order can only be marked as delivered when it's out for delivery.");
         }
     } else if (newStatus == OrderStatus.CANCELLED) {
         // Both customer and restaurant owner can cancel
         boolean isCustomer = updater.getRole() == Role.USER && order.getUser().getId().equals(updaterId);
         boolean isRestaurantOwner = updater.getRole() == Role.HOTEL && order.getRestaurant().getHotelOwner().getId().equals(updaterId);
         
         if (!isCustomer && !isRestaurantOwner) {
             throw new UnauthorizedActionException("You are not authorized to cancel this order.");
         }
         
         // Customer can only cancel if order is still PLACED
         if (isCustomer && order.getStatus() != OrderStatus.PLACED) {
             throw new UnauthorizedActionException("Order cannot be cancelled after preparation has started.");
         }
     } else {
         // PREPARING and OUT_FOR_DELIVERY can only be set by restaurant owner
         if (updater.getRole() != Role.HOTEL || !order.getRestaurant().getHotelOwner().getId().equals(updaterId)) {
             throw new UnauthorizedActionException("Only the restaurant owner can update order preparation status.");
         }
     }

     order.setStatus(newStatus);
     return orderRepository.save(order);
 }
 
 public List<Order> getOrdersByUser(Long userId) {
     return orderRepository.findByUserId(userId);
 }
 
 public List<Order> getOrdersByRestaurant(Long restaurantId) {
     return orderRepository.findByRestaurantId(restaurantId);
 }
}
