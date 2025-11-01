package dto;

import java.util.List;

import entity.OrderItem;

//package com.foodapp.food_ordering_system.dto;

import lombok.Data;

@Data // Simple DTO for receiving data
public class OrderRequestDto {
 // Only the fields the user is sending in the POST request:
 
 // The items and their details (quantity, assumed price/name)
 private List<OrderItem> items;

 public OrderItem[] getItems() {
	// TODO Auto-generated method stub
	 return items.toArray(new OrderItem[0]);
 }

 
 // We get userId and restaurantId from the URL path, not the body, but 
 // you might add notes/instructions here in a real DTO.
}
