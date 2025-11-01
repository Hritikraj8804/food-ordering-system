package entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

//entity/Order.java
@Entity
@Table(name = "orders")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "user_id")
 private User user; 

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "restaurant_id")
 private Restaurant restaurant; 

 private Double totalAmount;
 
 @Enumerated(EnumType.STRING)
 private OrderStatus status = OrderStatus.PLACED; 

 // OneToMany: One Order has Many OrderItems
 // cascade = ALL ensures that saving/deleting the Order saves/deletes the Items
 @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
 @com.fasterxml.jackson.annotation.JsonManagedReference
 private List<OrderItem> items = new ArrayList<>(); 
 
 // --- Helper Method for managing bi-directional relationship ---
 public void addItem(OrderItem item) {
     items.add(item);
     item.setOrder(this);
 }

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public User getUser() {
	return user;
 }

 public void setUser(User user) {
	this.user = user;
 }

 public Restaurant getRestaurant() {
	return restaurant;
 }

 public void setRestaurant(Restaurant restaurant) {
	this.restaurant = restaurant;
 }

 public Double getTotalAmount() {
	return totalAmount;
 }

 public void setTotalAmount(Double totalAmount) {
	this.totalAmount = totalAmount;
 }

 public OrderStatus getStatus() {
	return status;
 }

 public void setStatus(OrderStatus status) {
	this.status = status;
 }

 public List<OrderItem> getItems() {
	return items;
 }

 public void setItems(List<OrderItem> items) {
	this.items = items;
 }
 
 
}
