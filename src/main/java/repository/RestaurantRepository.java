package repository;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {}
