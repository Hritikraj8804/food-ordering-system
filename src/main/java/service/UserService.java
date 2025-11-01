package service;

import org.springframework.stereotype.Service;

import entity.Role;
import entity.User;
import exception.ResourceNotFoundException;
import repository.UserRepository;
import java.util.Optional;

@Service
public class UserService {

 private final UserRepository userRepository;

 // Dependency Injection via constructor (Best Practice!)
 public UserService(UserRepository userRepository) {
     this.userRepository = userRepository;
 }

 public User registerUser(User user) {
     // Validation: Check for existing email 
     userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
         throw new IllegalArgumentException("Email address is already registered.");
     });

     // Set default role if none specified
     if (user.getRole() == null) {
         user.setRole(Role.USER);
     }
     
     // Security Note: Here is where a PasswordEncoder would hash the password!
     
     return userRepository.save(user);
 }

 public User getUserById(Long id) {
     return userRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
 }
}


