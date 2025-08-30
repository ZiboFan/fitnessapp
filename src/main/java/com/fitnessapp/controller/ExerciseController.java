package com.fitnessapp.controller;

import com.fitnessapp.model.Exercise;
import com.fitnessapp.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
 
// All by myself
// Controller class
// Handles exercise-related requests
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseRepository exerciseRepository;// Exercise repository for accessing exercise data


    // Get all exercises
    @GetMapping
    public List<Exercise> getAll() {
        return exerciseRepository.findAll();
    }
}