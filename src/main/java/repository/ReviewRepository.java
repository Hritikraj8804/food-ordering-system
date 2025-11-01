package repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByOrderId(Long orderId);
    Optional<Review> findByOrderIdAndUserId(Long orderId, Long userId);
    List<Review> findByRestaurantId(Long restaurantId);
    @Query("SELECT r FROM Review r JOIN r.order o JOIN o.items oi WHERE oi.menuItem.id = :itemId")
    List<Review> findByMenuItemId(@Param("itemId") Long itemId);
}