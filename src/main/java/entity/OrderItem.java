package entity;

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
public class OrderItem {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String itemName;
 private Integer quantity;
 private Double price;

 // ManyToOne: Many OrderItems belong to One Order
 @ManyToOne(fetch = FetchType.LAZY) 
 @JoinColumn(name = "order_id", nullable = false)
 private Order order; // Bi-directional link back to the Order

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public String getItemName() {
	return itemName;
 }

 public void setItemName(String itemName) {
	this.itemName = itemName;
 }

 public Integer getQuantity() {
	return quantity;
 }

 public void setQuantity(Integer quantity) {
	this.quantity = quantity;
 }

 public Double getPrice() {
	return price;
 }

 public void setPrice(Double price) {
	this.price = price;
 }

 public Order getOrder() {
	return order;
 }

 public void setOrder(Order order) {
	this.order = order;
 }
 
 
}
