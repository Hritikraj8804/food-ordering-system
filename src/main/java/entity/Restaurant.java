package entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

//entity/Restaurant.java
@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Restaurant {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String name;
 private String address;
 private String cuisineType;
 
 // ManyToOne: Many Restaurants can be owned by One HOTEL User
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "hotel_id", nullable = false) // Maps to the FK in the DB schema
 private User hotelOwner;

 public Long getId() {
	return id;
 }

 public void setId(Long id) {
	this.id = id;
 }

 public String getName() {
	return name;
 }

 public void setName(String name) {
	this.name = name;
 }

 public String getAddress() {
	return address;
 }

 public void setAddress(String address) {
	this.address = address;
 }

 public String getCuisineType() {
	return cuisineType;
 }

 public void setCuisineType(String cuisineType) {
	this.cuisineType = cuisineType;
 }

 public User getHotelOwner() {
	return hotelOwner;
 }

 public void setHotelOwner(User hotelOwner) {
	this.hotelOwner = hotelOwner;
 } 
 
 
}