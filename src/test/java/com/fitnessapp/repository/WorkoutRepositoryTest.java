package com.fitnessapp.repository;

import com.fitnessapp.model.Exercise;
import com.fitnessapp.model.User;
import com.fitnessapp.model.Workout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Use ChatGpt to learn how to test the repository
// Test class for WorkoutRepository
@DataJpaTest
class WorkoutRepositoryTest {

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Query by weight desc and popular exercise stats")
    void queryAndStats() {
        User u = userRepository.save(new User("stat", "p123456"));
        Exercise e = new Exercise();
        e.setName("Squat");
        e.setCategory("legs");
        e = exerciseRepository.save(e);

        Workout w1 = new Workout();
        w1.setUser(u);
        w1.setUsername(u.getUsername());
        w1.setExercise(e);
        w1.setWeight(100);
        w1.setReps(5);
        w1.setTimestamp(LocalDateTime.now());
        workoutRepository.save(w1);

        Workout w2 = new Workout();
        w2.setUser(u);
        w2.setUsername(u.getUsername());
        w2.setExercise(e);
        w2.setWeight(120);
        w2.setReps(3);
        w2.setTimestamp(LocalDateTime.now());
        workoutRepository.save(w2);

        List<Workout> ordered = workoutRepository.findByUsernameAndExerciseOrderByWeightDesc(u.getUsername(), e);
        assertThat(ordered).hasSize(2);
        assertThat(ordered.get(0).getWeight()).isEqualTo(120);

        List<Object[]> stats = workoutRepository.findMostUsedExercisesByCategory(u.getUsername(), "legs");
        assertThat(stats).isNotEmpty();
        assertThat(stats.get(0)[0]).isInstanceOf(Exercise.class);
    }
}

