package repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.OrderItem;

/**
 * Repository for handling CRUD operations on the OrderItem entity.
 * This repository inherits all standard methods (save, findById, findAll, etc.) 
 * from JpaRepository<Entity, PrimaryKeyType>.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // No custom methods needed! 
    // Order items are managed automatically when the parent Order is saved/loaded, 
    // thanks to the CascadeType.ALL mapping on the Order entity.
}