package com.fitnessapp.controller;

import com.fitnessapp.model.User;
import com.fitnessapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

// All by myself
// Controller class
// Handles user-related requests
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Initialize user (auto-creation disabled; instruct to use registration endpoint)
    @PostMapping("/{username}/init")
    public ResponseEntity<String> initUser(@PathVariable String username) {
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            return ResponseEntity.ok("User already exists");
        }
        return ResponseEntity.status(400).body("Please create an account via: /api/auth/register");
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by username
    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userRepository.findByUsername(username);
    }

    // Update user's last login time
    @PutMapping("/{username}/last-login")
    public String updateLastLogin(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            return "Last login time updated successfully";
        }
        return "User not found";
    }

    // Get user statistics
    @GetMapping("/{username}/stats")
    public Object getUserStats(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "User not found";
        }
        
        // Add more user statistics here as needed
        record Stats(String username, String nickname, LocalDateTime createdAt, LocalDateTime lastLoginAt) {}
        return new Stats(user.getUsername(), user.getNickname(), user.getCreatedAt(), user.getLastLoginAt());
    }

    // Update user profile (user must exist)
    @PutMapping("/{username}/profile")
    public ResponseEntity<String> updateProfile(@PathVariable String username, @RequestBody User profileData) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found, please register first");
        }

        // Update profile fields
        if (profileData.getNickname() != null) {
            user.setNickname(profileData.getNickname());
        }
        if (profileData.getEmail() != null) {
            user.setEmail(profileData.getEmail());
        }
        if (profileData.getHeight() != null) {
            user.setHeight(profileData.getHeight());
        }
        if (profileData.getWeight() != null) {
            user.setWeight(profileData.getWeight());
        }
        if (profileData.getAge() != null) {
            user.setAge(profileData.getAge());
        }
        if (profileData.getGender() != null) {
            user.setGender(profileData.getGender());
        }

        userRepository.save(user);
        return ResponseEntity.ok("Profile updated successfully");
    }

    // Get full user profile (including health data; user must exist)
    @GetMapping("/{username}/profile")
    public ResponseEntity<Object> getProfile(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found, please register first");
        }

        // Use record for a clean DTO to avoid unused field warnings on anonymous classes
        record ProfileDTO(
            String username,
            String nickname,
            String email,
            Double height,
            Double weight,
            Integer age,
            String gender,
            Double bmi,
            String bmiCategory,
            Double bmr,
            LocalDateTime createdAt,
            LocalDateTime lastLoginAt
        ) {}

        ProfileDTO dto = new ProfileDTO(
            user.getUsername(),
            user.getNickname(),
            user.getEmail(),
            user.getHeight(),
            user.getWeight(),
            user.getAge(),
            user.getGender(),
            user.getBMI(),
            user.getBMICategory(),
            user.getBMR(),
            user.getCreatedAt(),
            user.getLastLoginAt()
        );
        return ResponseEntity.ok(dto);
    }
} 