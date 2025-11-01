package controller;

import entity.Address;
import service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "http://localhost:3000")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Address>> getUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }
    
    @PostMapping("/user/{userId}")
    public ResponseEntity<Address> saveAddress(@PathVariable Long userId, @Valid @RequestBody Address address) {
        return ResponseEntity.ok(addressService.saveAddress(userId, address));
    }
    
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }
}