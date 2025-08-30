package com.fitnessapp;

import com.fitnessapp.model.Exercise;
import com.fitnessapp.model.User;
import com.fitnessapp.model.Workout;
import com.fitnessapp.repository.ExerciseRepository;
import com.fitnessapp.repository.UserRepository;
import com.fitnessapp.repository.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

// Use ChatGpt to learn how to test the performance
// Test class for Performance
@SpringBootTest
@ActiveProfiles("test")
class PerformanceTest {

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
        // Clean up test data
        workoutRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User("perfuser", "password123");
        testUser.setHeight(175.0);
        testUser.setWeight(70.0);
        testUser.setAge(25);
        testUser.setGender("Male");
        testUser = userRepository.save(testUser);

        // Create test exercise
        testExercise = new Exercise();
        testExercise.setName("Bench Press");
        testExercise.setCategory("chest");
        testExercise = exerciseRepository.save(testExercise);
    }

    @Test
    @DisplayName("Database query performance under large dataset")
    void testDatabasePerformanceWithLargeDataset() {
        // Create large dataset
        List<Workout> workouts = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now();

        for (int i = 0; i < 1000; i++) {
            Workout workout = new Workout();
            workout.setUser(testUser);
            workout.setUsername(testUser.getUsername());
            workout.setExercise(testExercise);
            workout.setWeight(50 + (i % 50)); // 50-100kg
            workout.setReps(5 + (i % 15)); // 5-20 reps
            workout.setTimestamp(baseTime.minusDays(i));
            workouts.add(workout);
        }

        long startTime = System.currentTimeMillis();
        workoutRepository.saveAll(workouts);
        long saveTime = System.currentTimeMillis() - startTime;

        // Test query performance
        startTime = System.currentTimeMillis();
        List<Workout> userHistory = workoutRepository.findByUsernameOrderByTimestampDesc(testUser.getUsername());
        long queryTime = System.currentTimeMillis() - startTime;

        // Test complex query performance
        startTime = System.currentTimeMillis();
        List<Workout> orderedWorkouts = workoutRepository.findByUsernameAndExerciseOrderByWeightDesc(testUser.getUsername(), testExercise);
        long complexQueryTime = System.currentTimeMillis() - startTime;

        // Assertions
        assertThat(userHistory).hasSize(1000);
        assertThat(orderedWorkouts).hasSize(1000);
        assertThat(orderedWorkouts.get(0).getWeight()).isEqualTo(99); // Highest weight should be first

        // Performance assertions (adjust thresholds based on your system)
        assertThat(saveTime).isLessThan(5000); // Should save 1000 records in under 5 seconds
        assertThat(queryTime).isLessThan(1000); // Should query in under 1 second
        assertThat(complexQueryTime).isLessThan(1000); // Should complex query in under 1 second

        System.out.println("Performance Results:");
        System.out.println("Save 1000 records: " + saveTime + "ms");
        System.out.println("Query user history: " + queryTime + "ms");
        System.out.println("Complex query with ordering: " + complexQueryTime + "ms");
    }

    @Test
    @DisplayName("Concurrent user access performance")
    void testConcurrentUserAccess() throws Exception {
        // Create multiple users
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User("concurrent" + i, "password" + i);
            user.setHeight(170.0 + i);
            user.setWeight(65.0 + i);
            user.setAge(20 + i);
            user.setGender(i % 2 == 0 ? "Male" : "Female");
            users.add(userRepository.save(user));
        }

        // Create exercises for each user
        List<Exercise> exercises = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Exercise exercise = new Exercise();
            exercise.setName("Exercise " + i);
            exercise.setCategory("category" + i);
            exercises.add(exerciseRepository.save(exercise));
        }

        // Simulate concurrent workout logging
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Long>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (User user : users) {
            for (Exercise exercise : exercises) {
                CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                    Workout workout = new Workout();
                    workout.setUser(user);
                    workout.setUsername(user.getUsername());
                    workout.setExercise(exercise);
                    workout.setWeight(50 + (int) (Math.random() * 50));
                    workout.setReps(5 + (int) (Math.random() * 15));
                    workout.setTimestamp(LocalDateTime.now());
                    return workoutRepository.save(workout).getId();
                }, executor);
                futures.add(future);
            }
        }

        // Wait for all operations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
        long totalTime = System.currentTimeMillis() - startTime;

        // Verify all operations completed
        long totalWorkouts = workoutRepository.count();
        assertThat(totalWorkouts).isEqualTo(50); // 10 users * 5 exercises

        // Performance assertion
        assertThat(totalTime).isLessThan(10000); // Should complete in under 10 seconds

        System.out.println("Concurrent Performance Results:");
        System.out.println("Total concurrent operations: 50");
        System.out.println("Total time: " + totalTime + "ms");
        System.out.println("Average time per operation: " + (totalTime / 50.0) + "ms");

        executor.shutdown();
    }

    @Test
    @DisplayName("Memory usage under stress")
    void testMemoryUsageUnderStress() {
        // Monitor memory usage
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Create stress data
        List<Workout> stressWorkouts = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            Workout workout = new Workout();
            workout.setUser(testUser);
            workout.setUsername(testUser.getUsername());
            workout.setExercise(testExercise);
            workout.setWeight(50 + (i % 50));
            workout.setReps(5 + (i % 15));
            workout.setTimestamp(LocalDateTime.now().minusDays(i % 365));
            stressWorkouts.add(workout);
        }

        // Save stress data
        workoutRepository.saveAll(stressWorkouts);

        // Query stress data
        List<Workout> allWorkouts = workoutRepository.findByUsernameOrderByTimestampDesc(testUser.getUsername());

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;

        // Assertions
        assertThat(allWorkouts).hasSize(5000);
        assertThat(memoryUsed).isLessThan(100 * 1024 * 1024); // Should use less than 100MB additional memory

        System.out.println("Memory Usage Results:");
        System.out.println("Initial memory: " + (initialMemory / 1024 / 1024) + "MB");
        System.out.println("Final memory: " + (finalMemory / 1024 / 1024) + "MB");
        System.out.println("Additional memory used: " + (memoryUsed / 1024 / 1024) + "MB");
    }
}
