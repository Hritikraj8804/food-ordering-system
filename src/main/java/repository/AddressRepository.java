package repository;

import entity.Address;
import entity.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIsDefaultDesc(Long userId);
    List<Address> findByUserIdAndType(Long userId, AddressType type);
    Address findByUserIdAndIsDefaultTrue(Long userId);
}