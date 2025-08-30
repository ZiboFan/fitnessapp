package com.fitnessapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessapp.model.User;
import com.fitnessapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// By myself
// Test class for UserController
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Get and update user profile")
    void getAndUpdateProfile() throws Exception {
        // Register a user first
        User u = new User("u3", "pass123");
        u.setNickname("nick");
        userRepository.save(u);

        mockMvc.perform(get("/api/users/u3/profile"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("u3"));

        User update = new User();
        update.setNickname("newNick");
        update.setEmail("e@x.com");
        update.setHeight(180.0);
        update.setWeight(80.0);
        update.setAge(30);
        update.setGender("Male");

        mockMvc.perform(put("/api/users/u3/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk())
            .andExpect(content().string("Profile updated successfully"));

        User after = userRepository.findByUsername("u3");
        assertThat(after.getNickname()).isEqualTo("newNick");
        assertThat(after.getEmail()).isEqualTo("e@x.com");
    }
}

