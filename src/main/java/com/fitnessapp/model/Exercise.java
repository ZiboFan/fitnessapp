package com.fitnessapp.model;

import jakarta.persistence.*;
// All by myself
// Entity class
// Represents an exercise
@Entity
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // exercise name
    private String category; // exercise category (e.g., chest, back, shoulder)

    // No-args constructor (required by JPA)
    public Exercise() {
    }

    // All-args constructor
    public Exercise(Long id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}