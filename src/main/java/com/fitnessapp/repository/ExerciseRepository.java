package com.fitnessapp.repository;

import com.fitnessapp.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

// All by myself
// Repository interface
// For accessing exercise data
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
