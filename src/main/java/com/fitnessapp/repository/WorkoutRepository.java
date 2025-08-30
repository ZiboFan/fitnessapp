package com.fitnessapp.repository;

import com.fitnessapp.model.Workout;
import com.fitnessapp.model.Exercise;
import com.fitnessapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


// All by myself
// Repository interface
// For accessing workout data
public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    List<Workout> findByUsernameAndExerciseOrderByWeightDesc(String username, Exercise exercise);
    List<Workout> findByUsernameOrderByTimestampDesc(String username);
    
    // Find most used exercises by category for a user
    @Query("SELECT w.exercise, COUNT(w) as count FROM Workout w " +
           "WHERE w.username = :username AND w.exercise.category = :category " +
           "GROUP BY w.exercise ORDER BY count DESC")
    List<Object[]> findMostUsedExercisesByCategory(@Param("username") String username, @Param("category") String category);

    // Find global popular exercises by category (all users)
    @Query("SELECT w.exercise, COUNT(w) as count FROM Workout w " +
           "WHERE w.exercise.category = :category " +
           "GROUP BY w.exercise ORDER BY count DESC")
    List<Object[]> findGlobalPopularExercisesByCategory(@Param("category") String category);

    // Find popular exercises by category excluding a specific user
    @Query("SELECT w.exercise, COUNT(w) as count FROM Workout w " +
           "WHERE w.exercise.category = :category AND w.username <> :username " +
           "GROUP BY w.exercise ORDER BY count DESC")
    List<Object[]> findPopularExercisesByCategoryExcludingUser(@Param("category") String category, @Param("username") String username);
    
    // Find all workouts by user ordered by timestamp desc
    List<Workout> findByUserOrderByTimestampDesc(User user);
    
    // Find workouts by user and exercise ordered by weight desc
    List<Workout> findByUserAndExerciseOrderByWeightDesc(User user, Exercise exercise);
}
