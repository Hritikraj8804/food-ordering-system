package service;

import entity.Address;
import entity.User;
import repository.AddressRepository;
import repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AddressService {
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
    }
    
    @Transactional
    public Address saveAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        address.setUser(user);
        
        if (address.isDefault()) {
            List<Address> userAddresses = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
            userAddresses.forEach(addr -> {
                addr.setDefault(false);
                addressRepository.save(addr);
            });
        }
        
        return addressRepository.save(address);
    }
    
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }
}