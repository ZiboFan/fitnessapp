package com.fitnessapp.controller;

import com.fitnessapp.model.Workout;
import com.fitnessapp.model.Exercise;
import com.fitnessapp.model.User;
import com.fitnessapp.model.WorkoutSessionRequest;
import com.fitnessapp.model.WorkoutSession;
import com.fitnessapp.repository.ExerciseRepository;
import com.fitnessapp.repository.WorkoutRepository;
import com.fitnessapp.repository.WorkoutSessionRepository;
import com.fitnessapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

// All by myself
// Controller class
// Handles workout logging related requests
@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutRepository workoutRepository;// Workout repository for accessing workout data

    @Autowired
    private ExerciseRepository exerciseRepository;// Exercise repository for accessing exercise data
    
    @Autowired
    private UserRepository userRepository;// User repository for accessing user data

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository; // Workout session repository

    // Test database connection
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            long exerciseCount = exerciseRepository.count();
            long userCount = userRepository.count();
            long workoutCount = workoutRepository.count();
            
            return ResponseEntity.ok(String.format("Database connection OK! Exercise: %d, User: %d, Workout: %d", 
                exerciseCount, userCount, workoutCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Database connection failed: " + e.getMessage());
        }
    }

    @PostMapping("/log")// Log a workout
    public ResponseEntity<?> logWorkout(@RequestParam String username,
                               @RequestParam Long exerciseId,
                               @RequestParam double weight,
                               @RequestParam int reps,
                               @RequestParam(required = false, defaultValue = "0") int restSeconds) {
        try {
            System.out.println("Received workout log request: username=" + username + ", exerciseId=" + exerciseId + ", weight=" + weight + ", reps=" + reps);
            
            Optional<Exercise> exerciseOpt = exerciseRepository.findById(exerciseId);
            if (exerciseOpt.isEmpty()) {
                System.out.println("Exercise not found: " + exerciseId);
                return ResponseEntity.badRequest().body("Exercise not found. Please initialize exercise library first");
            }

            Exercise exercise = exerciseOpt.get();
            System.out.println("Found exercise: " + exercise.getName());
            
            List<Workout> history = workoutRepository.findByUsernameAndExerciseOrderByWeightDesc(username, exercise);
            boolean isPR = history.isEmpty() || weight > history.get(0).getWeight();

            // Find existing registered user (no implicit creation)
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, please register first");
            }

            Workout workout = new Workout();
            workout.setUsername(username);
            workout.setUser(user);
            workout.setExercise(exercise);
            workout.setWeight(weight);
            workout.setReps(reps);
            workout.setTimestamp(LocalDateTime.now());
            workout.setPersonalRecord(isPR);
            workout.setRestSeconds(restSeconds);
            workout.setDuration(0); // Set default workout duration to 0 seconds

            System.out.println("Preparing to save workout: " + workout.toString());
            Workout savedWorkout = workoutRepository.save(workout);
            System.out.println("Workout saved successfully: " + savedWorkout.getId());
            return ResponseEntity.ok(savedWorkout);
            
        } catch (Exception e) {
            System.err.println("Error saving workout: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Logging failed: " + e.getMessage());
        }
    }

    // Initialize test data
    @PostMapping("/init-test-data")
    public String initTestData() {
        String username = "testuser";
        
        // Ensure user exists
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User(username, "testpass");
            user.setNickname("Test User");
            user.setHeight(175.0);
            user.setWeight(70.0);
            user.setAge(25);
            user.setGender("Male");
            userRepository.save(user);
        }
        
        // Get some exercises
        List<Exercise> exercises = exerciseRepository.findAll();
        if (exercises.isEmpty()) {
            return "No exercises found. Please initialize exercises first.";
        }

        // Create test data for the last 30 days
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random();
        
        for (int day = 0; day < 30; day++) {
            LocalDateTime date = now.minusDays(day);
            
            // 1-3 workout records per day
            int dailyWorkouts = random.nextInt(3) + 1;
            
            for (int i = 0; i < dailyWorkouts; i++) {
                Exercise exercise = exercises.get(random.nextInt(exercises.size()));
                double weight = 50 + random.nextInt(50); // 50-100kg
                int reps = 5 + random.nextInt(10); // 5-15 reps
                
                // Check if PR
                List<Workout> history = workoutRepository.findByUsernameAndExerciseOrderByWeightDesc(username, exercise);
                boolean isPR = history.isEmpty() || weight > history.get(0).getWeight();
                
                Workout workout = new Workout();
                workout.setUsername(username);
                workout.setUser(user);
                workout.setExercise(exercise);
                workout.setWeight(weight);
                workout.setReps(reps);
                workout.setTimestamp(date);
                workout.setPersonalRecord(isPR);
                workout.setRestSeconds(90);
                workout.setDuration(0); // Set default workout duration to 0 seconds
                
                workoutRepository.save(workout);
            }
        }
        
        return "Test data initialized successfully!";
    }
    // Get user's workout history
    @GetMapping("/history")
    public List<Workout> getUserHistory(@RequestParam String username, @RequestParam(required = false) Long exerciseId) {
        if (exerciseId != null) {
            Exercise exercise = exerciseRepository.findById(exerciseId).orElseThrow();
            return workoutRepository.findByUsernameAndExerciseOrderByWeightDesc(username, exercise);
        } else {
            // Get all user's workouts by timestamp desc
            return workoutRepository.findByUsernameOrderByTimestampDesc(username);
        }
    }

    // Get user's most used exercises
    @GetMapping("/most-used")
    public List<Exercise> getMostUsedExercises(@RequestParam String username, @RequestParam String category) {
        List<Object[]> results = workoutRepository.findMostUsedExercisesByCategory(username, category);
        return results.stream()
                .map(result -> (Exercise) result[0])
                .toList();
    }

    // Save workout session (persist after completion)
    @PostMapping("/session")
    public ResponseEntity<String> saveWorkoutSession(@RequestBody WorkoutSessionRequest request) {
        try {
            String username = request.getUsername();
            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().body("Username cannot be empty");
            }

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found, please register first");
            }

            WorkoutSession session = new WorkoutSession();
            session.setUser(user);
            session.setUsername(username);
            session.setDurationText(request.getDuration());

            // Try to convert duration text to seconds (format can be HH:MM:SS or MM:SS)
            Integer durationSeconds = null;
            try {
                String[] parts = request.getDuration() != null ? request.getDuration().split(":") : new String[0];
                if (parts.length == 3) {
                    durationSeconds = Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
                } else if (parts.length == 2) {
                    durationSeconds = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
                }
            } catch (Exception ignore) {}
            session.setDurationSeconds(durationSeconds);

            session.setTotalSets(request.getTotalSets());
            session.setTotalVolume(request.getTotalVolume());
            session.setTotalReps(request.getTotalReps());
            session.setPrCount(request.getPrCount());

            if (request.getStartTime() > 0) {
                session.setStartTime(java.time.LocalDateTime.ofEpochSecond(request.getStartTime() / 1000, 0, java.time.ZoneOffset.UTC));
            }
            if (request.getEndTime() > 0) {
                session.setEndTime(java.time.LocalDateTime.ofEpochSecond(request.getEndTime() / 1000, 0, java.time.ZoneOffset.UTC));
            }
            session.setCreatedAt(java.time.LocalDateTime.now());

            workoutSessionRepository.save(session);
            return ResponseEntity.ok("Workout session saved successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save workout session: " + e.getMessage());
        }
    }

    // Get global popular exercises (by category)
    @GetMapping("/popular")
    public List<Exercise> getGlobalPopularExercises(@RequestParam String category) {
        List<Object[]> results = workoutRepository.findGlobalPopularExercisesByCategory(category);
        return results.stream().map(r -> (Exercise) r[0]).toList();
    }

    // Get popular exercises (by category, excluding current user)
    @GetMapping("/popular-exclude-self")
    public List<Exercise> getPopularExercisesExcludeSelf(@RequestParam String category, @RequestParam String username) {
        List<Object[]> results = workoutRepository.findPopularExercisesByCategoryExcludingUser(category, username);
        return results.stream().map(r -> (Exercise) r[0]).toList();
    }
}