package service;

import dto.AuthResponse;
import dto.LoginRequest;
import entity.User;
import repository.UserRepository;
import security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthResponse register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        String token = jwtUtil.generateToken(
            savedUser.getEmail(), 
            savedUser.getRole().toString(), 
            savedUser.getId()
        );
        
        return new AuthResponse(
            token, 
            savedUser.getEmail(), 
            savedUser.getName(), 
            savedUser.getRole().toString(), 
            savedUser.getId()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        System.out.println("Login attempt for email: " + request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                System.out.println("User not found: " + request.getEmail());
                return new RuntimeException("Invalid credentials");
            });
        
        System.out.println("Found user: " + user.getEmail() + " with stored password: " + user.getPassword());
        System.out.println("Provided password: " + request.getPassword());
        
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("Password matches: " + passwordMatches);
        
        if (!passwordMatches) {
            System.out.println("Password mismatch for user: " + request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(
            user.getEmail(), 
            user.getRole().toString(), 
            user.getId()
        );
        
        return new AuthResponse(
            token, 
            user.getEmail(), 
            user.getName(), 
            user.getRole().toString(), 
            user.getId()
        );
    }
}