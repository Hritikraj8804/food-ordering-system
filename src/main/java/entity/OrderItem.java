package entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;
    private Double priceAtOrder;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "menu_item_id", nullable = true)
    @JsonIgnore
    private MenuItem menuItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(Double priceAtOrder) { this.priceAtOrder = priceAtOrder; }
    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    @Override
    public String toString() {
        return "OrderItem{id=" + id + ", quantity=" + quantity + ", priceAtOrder=" + priceAtOrder + "}";
    }
}