package com.fitnessapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnessapp.model.Exercise;
import com.fitnessapp.model.User;
import com.fitnessapp.repository.ExerciseRepository;
import com.fitnessapp.repository.UserRepository;
import com.fitnessapp.repository.WorkoutRepository;
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
class UsabilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private WorkoutRepository workoutRepository;

    private User testUser;
    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        workoutRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();
        
        testUser = new User("usabilityuser", "usabilitypass123");
        testUser.setEmail("usability@test.com");
        testUser.setNickname("Usability Test User");
        testUser.setHeight(175.0);
        testUser.setWeight(70.0);
        testUser.setAge(25);
        testUser.setGender("Male");
        testUser = userRepository.save(testUser);

        testExercise = new Exercise();
        testExercise.setName("Squat");
        testExercise.setCategory("legs");
        testExercise = exerciseRepository.save(testExercise);
    }

    @Test
    @DisplayName("Task completion efficiency test")
    void testTaskCompletionEfficiency() throws Exception {
        // Test how efficiently users can complete common tasks
        
        // Task 1: User registration
        long startTime = System.currentTimeMillis();
        
        Map<String, Object> body = new HashMap<>();
        body.put("username", "efficiencyuser");
        body.put("password", "password123");
        body.put("email", "efficiency@test.com");
        body.put("nickname", "Efficiency User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk());

        long registrationTime = System.currentTimeMillis() - startTime;
        
        // Task 2: Workout logging
        startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/workouts/log")
                .param("username", "efficiencyuser")
                .param("exerciseId", String.valueOf(testExercise.getId()))
                .param("weight", "100")
                .param("reps", "10")
                .param("restSeconds", "90"))
            .andExpect(status().isOk());

        long workoutLogTime = System.currentTimeMillis() - startTime;
        
        // Task 3: View workout history
        startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/workouts/history")
                .param("username", "efficiencyuser"))
            .andExpect(status().isOk());

        long historyViewTime = System.currentTimeMillis() - startTime;

        // Performance assertions
        assertThat(registrationTime).isLessThan(2000); // Registration should complete in under 2 seconds
        assertThat(workoutLogTime).isLessThan(1000); // Workout logging should complete in under 1 second
        assertThat(historyViewTime).isLessThan(1000); // History viewing should complete in under 1 second

        System.out.println("Task Completion Efficiency Results:");
        System.out.println("User registration: " + registrationTime + "ms");
        System.out.println("Workout logging: " + workoutLogTime + "ms");
        System.out.println("History viewing: " + historyViewTime + "ms");
    }

    @Test
    @DisplayName("Error handling usability test")
    void testErrorHandlingUsability() throws Exception {
        // Test how well the system handles errors and guides users
        
        // Test 1: Invalid password length
        Map<String, Object> body = new HashMap<>();
        body.put("username", "erroruser1");
        body.put("password", "123"); // Too short

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());

        // Test 2: Missing required fields
        body = new HashMap<>();
        body.put("username", ""); // Empty username
        body.put("password", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());

        // Test 3: Invalid exercise ID
        mockMvc.perform(post("/api/workouts/log")
                .param("username", "usabilityuser")
                .param("exerciseId", "99999") // Non-existent exercise
                .param("weight", "100")
                .param("reps", "10"))
            .andExpect(status().isBadRequest());

        // Verify no invalid users were created
        assertThat(userRepository.findByUsername("erroruser1")).isNull();
        assertThat(userRepository.findByUsername("")).isNull();
    }

    @Test
    @DisplayName("User interface consistency test")
    void testUserInterfaceConsistency() throws Exception {
        // Test that the interface provides consistent behavior and feedback
        
        // Test consistent response format for successful operations
        Map<String, Object> body = new HashMap<>();
        body.put("username", "consistencyuser");
        body.put("password", "password123");
        body.put("email", "consistency@test.com");
        body.put("nickname", "Consistency User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(content().string("Registration successful"));

        // Test consistent response format for workout logging
        mockMvc.perform(post("/api/workouts/log")
                .param("username", "consistencyuser")
                .param("exerciseId", String.valueOf(testExercise.getId()))
                .param("weight", "100")
                .param("reps", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").value("consistencyuser"));

        // Test consistent response format for history retrieval
        mockMvc.perform(get("/api/workouts/history")
                .param("username", "consistencyuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("User guidance and help test")
    void testUserGuidanceAndHelp() throws Exception {
        // Test that the system provides helpful guidance to users
        
        // Test that invalid inputs provide helpful error messages
        Map<String, Object> body = new HashMap<>();
        body.put("username", "guidanceuser");
        body.put("password", "123"); // Too short

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());

        // Test that missing required fields are properly identified
        body = new HashMap<>();
        body.put("username", "guidanceuser2");
        // Missing password

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest());

        // Test that the system guides users to provide valid data
        body = new HashMap<>();
        body.put("username", "guidanceuser3");
        body.put("password", "validpassword123");
        body.put("email", "guidance@test.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("User preference accommodation test")
    void testUserPreferenceAccommodation() throws Exception {
        // Test that the system accommodates different user preferences and needs
        
        // Test various user input preferences
        Map<String, Object> body = new HashMap<>();
        body.put("username", "preferenceuser");
        body.put("password", "password123");
        body.put("email", "preference@test.com");
        body.put("nickname", "Preference User");
        body.put("height", 160.0); // Different height
        body.put("weight", 55.0);  // Different weight
        body.put("age", 35);       // Different age
        body.put("gender", "Female"); // Different gender

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk());

        // Verify all preferences were accommodated
        User createdUser = userRepository.findByUsername("preferenceuser");
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getHeight()).isEqualTo(160.0);
        assertThat(createdUser.getWeight()).isEqualTo(55.0);
        assertThat(createdUser.getAge()).isEqualTo(35);
        assertThat(createdUser.getGender()).isEqualTo("Female");
    }

    @Test
    @DisplayName("Workflow completion test")
    void testWorkflowCompletion() throws Exception {
        // Test complete user workflows from start to finish
        
        // Complete workflow: Register -> Login -> Log workout -> View history
        
        // Step 1: User registration
        Map<String, Object> body = new HashMap<>();
        body.put("username", "workflowuser");
        body.put("password", "password123");
        body.put("email", "workflow@test.com");
        body.put("nickname", "Workflow User");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk());

        // Step 2: User login
        Map<String, Object> loginBody = new HashMap<>();
        loginBody.put("username", "workflowuser");
        loginBody.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginBody)))
            .andExpect(status().isOk());

        // Step 3: Log workout
        mockMvc.perform(post("/api/workouts/log")
                .param("username", "workflowuser")
                .param("exerciseId", String.valueOf(testExercise.getId()))
                .param("weight", "120")
                .param("reps", "8")
                .param("restSeconds", "120"))
            .andExpect(status().isOk());

        // Step 4: View workout history
        mockMvc.perform(get("/api/workouts/history")
                .param("username", "workflowuser"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].username").value("workflowuser"));

        // Verify workflow was completed successfully
        User user = userRepository.findByUsername("workflowuser");
        assertThat(user).isNotNull();
        
        long workoutCount = workoutRepository.count();
        assertThat(workoutCount).isGreaterThan(0);
    }

    @Test
    @DisplayName("User satisfaction metrics test")
    void testUserSatisfactionMetrics() throws Exception {
        // Test metrics that indicate user satisfaction
        
        // Test successful task completion rate
        int totalTasks = 0;
        int successfulTasks = 0;
        
        // Task 1: Registration
        totalTasks++;
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("username", "satisfactionuser");
            body.put("password", "password123");
            body.put("email", "satisfaction@test.com");
            body.put("nickname", "Satisfaction User");

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
            successfulTasks++;
        } catch (Exception e) {
            // Task failed
        }
        
        // Task 2: Workout logging
        totalTasks++;
        try {
            mockMvc.perform(post("/api/workouts/log")
                    .param("username", "satisfactionuser")
                    .param("exerciseId", String.valueOf(testExercise.getId()))
                    .param("weight", "100")
                    .param("reps", "10"))
                .andExpect(status().isOk());
            successfulTasks++;
        } catch (Exception e) {
            // Task failed
        }
        
        // Task 3: History viewing
        totalTasks++;
        try {
            mockMvc.perform(get("/api/workouts/history")
                    .param("username", "satisfactionuser"))
                .andExpect(status().isOk());
            successfulTasks++;
        } catch (Exception e) {
            // Task failed
        }

        // Calculate success rate
        double successRate = (double) successfulTasks / totalTasks;
        
        // Assertions
        assertThat(successRate).isGreaterThan(0.8); // Should have at least 80% success rate
        
        System.out.println("User Satisfaction Metrics:");
        System.out.println("Total tasks attempted: " + totalTasks);
        System.out.println("Successful tasks: " + successfulTasks);
        System.out.println("Success rate: " + (successRate * 100) + "%");
    }
}
