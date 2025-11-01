package dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    private Long id;
    private Double totalAmount;
    private String status;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private Long restaurantId;
    private String restaurantName;
    private List<OrderItemDto> items;

    public static class OrderItemDto {
        private Long id;
        private Integer quantity;
        private Double priceAtOrder;
        private String itemName;
        private Long menuItemId;

        public OrderItemDto() {}

        public OrderItemDto(Long id, Integer quantity, Double priceAtOrder, String itemName, Long menuItemId) {
            this.id = id;
            this.quantity = quantity;
            this.priceAtOrder = priceAtOrder;
            this.itemName = itemName;
            this.menuItemId = menuItemId;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getPriceAtOrder() { return priceAtOrder; }
        public void setPriceAtOrder(Double priceAtOrder) { this.priceAtOrder = priceAtOrder; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public Long getMenuItemId() { return menuItemId; }
        public void setMenuItemId(Long menuItemId) { this.menuItemId = menuItemId; }
    }

    public OrderDto() {}

    public OrderDto(Long id, Double totalAmount, String status, String deliveryAddress, LocalDateTime createdAt,
                   Long userId, String userName, Long restaurantId, String restaurantName, List<OrderItemDto> items) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.createdAt = createdAt;
        this.userId = userId;
        this.userName = userName;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = items;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}