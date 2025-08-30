package com.fitnessapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

// Use ChatGpt to generate this code and learn the logic for the tests
// Test class for AuthController
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Registration and login success flow")
    void registerThenLogin_shouldSucceed() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "u1");
        body.put("password", "secret1");
        body.put("email", "u1@example.com");
        body.put("nickname", "User1");
        body.put("age", 20);
        body.put("gender", "Male");
        body.put("height", 175.0);
        body.put("weight", 70.0);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(content().string("Registration successful"));

        assertThat(userRepository.findByUsername("u1")).isNotNull();

        Map<String, Object> login = new HashMap<>();
        login.put("username", "u1");
        login.put("password", "secret1");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(content().string("Login successful"));
    }

    @Test
    @DisplayName("Registration failed: password too short")
    void register_shouldFail_whenPasswordTooShort() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("username", "u2");
        body.put("password", "123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());
    }
}

