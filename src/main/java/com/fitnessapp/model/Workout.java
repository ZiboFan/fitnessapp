package com.fitnessapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// All by myself
// Entity class
// Represents a workout record
@Entity
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // related user

    @Column(nullable = false)
    private String username; // athlete username (kept for compatibility)

    private double weight;
    private int reps;
    private LocalDateTime timestamp;

    private boolean personalRecord;
    private int restSeconds;
    
    // Workout duration (seconds)
    private Integer duration;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public boolean isPersonalRecord() { return personalRecord; }
    public void setPersonalRecord(boolean personalRecord) { this.personalRecord = personalRecord; }

    public int getRestSeconds() { return restSeconds; }
    public void setRestSeconds(int restSeconds) { this.restSeconds = restSeconds; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    @Override
    public String toString() {
        return "Workout{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", weight=" + weight +
                ", reps=" + reps +
                ", timestamp=" + timestamp +
                ", personalRecord=" + personalRecord +
                ", restSeconds=" + restSeconds +
                ", duration=" + duration +
                '}';
    }
}