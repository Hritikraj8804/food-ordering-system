package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

//entity/OrderItem.java
@Entity
@Table(name = "order_items")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderItem {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private Integer quantity;
 private Double priceAtOrder; // Price when order was placed

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "menu_item_id", nullable = true)
 private MenuItem menuItem;

 // ManyToOne: Many OrderItems belong to One Order
 @ManyToOne(fetch = FetchType.LAZY) 
 @JoinColumn(name = "order_id", nullable = false)
 @com.fasterxml.jackson.annotation.JsonBackReference
 private Order order; // Bi-directional link back to the Order

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public Integer getQuantity() {
	return quantity;
 }

 public void setQuantity(Integer quantity) {
	this.quantity = quantity;
 }

 public Double getPriceAtOrder() {
	return priceAtOrder;
 }

 public void setPriceAtOrder(Double priceAtOrder) {
	this.priceAtOrder = priceAtOrder;
 }

 public MenuItem getMenuItem() {
	return menuItem;
 }

 public void setMenuItem(MenuItem menuItem) {
	this.menuItem = menuItem;
 }

 public Order getOrder() {
	return order;
 }

 public void setOrder(Order order) {
	this.order = order;
 }
 
 
}
