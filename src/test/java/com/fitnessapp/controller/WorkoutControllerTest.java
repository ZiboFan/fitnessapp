package com.fitnessapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessapp.model.Exercise;
import com.fitnessapp.model.User;
import com.fitnessapp.model.WorkoutSessionRequest;
import com.fitnessapp.repository.ExerciseRepository;
import com.fitnessapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc

// By myself
// Test class for WorkoutController
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    private void ensureBaseData() {
        if (userRepository.findByUsername("wu") == null) {
            userRepository.save(new User("wu", "p123456"));
        }
        if (exerciseRepository.count() == 0) {
            Exercise e = new Exercise();
            e.setName("Bench Press");
            e.setCategory("chest");
            exerciseRepository.save(e);
        }
    }

    @Test
    @DisplayName("Log workout, query history, and save session")
    void logHistoryAndSession() throws Exception {
        ensureBaseData();

        Long exerciseId = exerciseRepository.findAll().get(0).getId();

        mockMvc.perform(post("/api/workouts/log")
                .param("username", "wu")
                .param("exerciseId", String.valueOf(exerciseId))
                .param("weight", "80")
                .param("reps", "8")
                .param("restSeconds", "90"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(get("/api/workouts/history").param("username", "wu"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("wu"));

        WorkoutSessionRequest req = new WorkoutSessionRequest(
            "wu", "00:35:10", 6, 5000.0, 48, 1,
            System.currentTimeMillis() - 40_000, System.currentTimeMillis()
        );

        mockMvc.perform(post("/api/workouts/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(content().string("Workout session saved successfully"));
    }
}

