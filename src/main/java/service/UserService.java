package service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import entity.Role;
import entity.User;
import exception.ResourceNotFoundException;
import repository.UserRepository;

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

 public List<User> getAllUsers() {
     return userRepository.findAll();
 }

 public User getUserById(Long id) {
     System.out.println("Searching for user ID: " + id);
     List<User> allUsers = userRepository.findAll();
     System.out.println("All users in database: " + allUsers.size());
     for (User u : allUsers) {
         System.out.println("User ID: " + u.getId() + ", Email: " + u.getEmail());
     }
     return userRepository.findById(id)
             .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
 }
}


