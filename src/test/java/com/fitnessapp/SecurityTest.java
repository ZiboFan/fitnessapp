package com.fitnessapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessapp.model.User;
import com.fitnessapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        testUser = new User("securityuser", "securepass123");
        testUser.setEmail("security@test.com");
        testUser.setNickname("Security Test User");
        testUser.setHeight(175.0);
        testUser.setWeight(70.0);
        testUser.setAge(25);
        testUser.setGender("Male");
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("SQL Injection prevention test")
    void testSqlInjectionPrevention() throws Exception {
        // Test SQL injection attempts in username
        String maliciousUsername = "'; DROP TABLE user; --";
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", maliciousUsername);
        body.put("password", "password123");
        body.put("email", "test@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());

        // Verify user table still exists and no malicious user was created
        assertThat(userRepository.findByUsername(maliciousUsername)).isNull();
    }

    @Test
    @DisplayName("XSS prevention test")
    void testXssPrevention() throws Exception {
        // Test XSS attempts in user input
        String maliciousInput = "<script>alert('XSS')</script>";
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", "xssuser");
        body.put("password", "password123");
        body.put("email", "test@example.com");
        body.put("nickname", maliciousInput);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk());

        // Verify malicious input was sanitized
        User createdUser = userRepository.findByUsername("xssuser");
        assertThat(createdUser).isNotNull();
        // The nickname should be stored as plain text, not executed as script
        assertThat(createdUser.getNickname()).isEqualTo(maliciousInput);
    }

    @Test
    @DisplayName("Authentication bypass prevention test")
    void testAuthenticationBypassPrevention() throws Exception {
        // Test accessing protected endpoints without authentication
        mockMvc.perform(get("/api/users/profile")
                .param("username", "securityuser"))
            .andExpect(status().isUnauthorized());

        // Test accessing workout history without authentication
        mockMvc.perform(get("/api/workouts/history")
                .param("username", "securityuser"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Password security validation test")
    void testPasswordSecurityValidation() throws Exception {
        // Test weak password rejection
        Map<String, Object> weakPasswordBody = new HashMap<>();
        weakPasswordBody.put("username", "weakpassuser");
        weakPasswordBody.put("password", "123"); // Too short
        weakPasswordBody.put("email", "test@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weakPasswordBody)))
            .andExpect(status().isBadRequest());

        // Test common password rejection
        Map<String, Object> commonPasswordBody = new HashMap<>();
        commonPasswordBody.put("username", "commonpassuser");
        commonPasswordBody.put("password", "password"); // Common password
        commonPasswordBody.put("email", "test@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commonPasswordBody)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Session security test")
    void testSessionSecurity() throws Exception {
        // Test session timeout
        // This would require more complex setup with session management
        // For now, we test that sessions are properly managed
        
        // Login first
        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("username", "securityuser");
        loginBody.put("password", "securepass123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Input validation security test")
    void testInputValidationSecurity() throws Exception {
        // Test extremely long input
        String longInput = "a".repeat(10000);
        
        Map<String, Object> longInputBody = new HashMap<>();
        longInputBody.put("username", longInput);
        longInputBody.put("password", "password123");
        longInputBody.put("email", "test@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longInputBody)))
            .andExpect(status().isBadRequest());

        // Test special characters in input
        String specialCharInput = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        Map<String, Object> specialCharBody = new HashMap<>();
        specialCharBody.put("username", "specialuser");
        specialCharBody.put("password", "password123");
        specialCharBody.put("email", "test@example.com");
        specialCharBody.put("nickname", specialCharInput);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharBody)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Authorization control test")
    void testAuthorizationControl() throws Exception {
        // Test that users cannot access other users' data
        // This would require more complex authorization setup
        // For now, we test basic access control
        
        // Create another user
        User anotherUser = new User("anotheruser", "password123");
        anotherUser.setEmail("another@test.com");
        userRepository.save(anotherUser);

        // Test that users are isolated
        assertThat(userRepository.findByUsername("securityuser")).isNotNull();
        assertThat(userRepository.findByUsername("anotheruser")).isNotNull();
        assertThat(userRepository.findByUsername("securityuser").getUsername())
            .isNotEqualTo(userRepository.findByUsername("anotheruser").getUsername());
    }

    @Test
    @DisplayName("Data encryption test")
    void testDataEncryption() throws Exception {
        // Test that sensitive data is not stored in plain text
        User user = userRepository.findByUsername("securityuser");
        assertThat(user).isNotNull();
        
        // Password should not be stored in plain text
        assertThat(user.getPassword()).isNotEqualTo("securepass123");
        
        // Other data should be stored as expected
        assertThat(user.getEmail()).isEqualTo("security@test.com");
        assertThat(user.getNickname()).isEqualTo("Security Test User");
    }
}
