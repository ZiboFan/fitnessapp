package com.fitnessapp.controller;

import com.fitnessapp.model.User;
import com.fitnessapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


// All by myself
// Controller class
// Handles user registration and login requests
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;// User repository for accessing user data

    // Register request DTO
    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String nickname;
        private Integer age;
        private String gender;
        private Double height;
        private Double weight;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }

        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }

        public Double getWeight() { return weight; }
        public void setWeight(Double weight) { this.weight = weight; }
    }

    @PostMapping("/register")// Register user
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        try {
            // Validate required fields
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username cannot be empty");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password cannot be empty");
            }
            if (request.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body("Password must be at least 6 characters");
            }

            // Check if username already exists
            if (userRepository.findByUsername(request.getUsername()) != null) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            // Create new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setNickname(request.getNickname());
            user.setAge(request.getAge());
            user.setGender(request.getGender());
            user.setHeight(request.getHeight());
            user.setWeight(request.getWeight());
            user.setCreatedAt(LocalDateTime.now());
            user.setLastLoginAt(LocalDateTime.now());

            // Save user to database
            userRepository.save(user);
            return ResponseEntity.ok("Registration successful");
            
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")// Login user
    public ResponseEntity<String> loginUser(@RequestBody User user) {
        try {
            User existingUser = userRepository.findByUsername(user.getUsername());
            if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
                return ResponseEntity.badRequest().body("Invalid username or password");
            }
            // Update last login time
            existingUser.setLastLoginAt(LocalDateTime.now());
            userRepository.save(existingUser);
            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}