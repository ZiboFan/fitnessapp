package com.fitnessapp.model;

import java.util.*;
// All by myself
// Utility class
// Represents a simple exercise library
public class ExerciseLibrary {

    private static final Map<String, List<ExerciseItem>> library = new LinkedHashMap<>();

    static {
        library.put("chest", Arrays.asList(
            new ExerciseItem(1L, "Barbell Bench Press"),
            new ExerciseItem(2L, "Incline Dumbbell Press"),
            new ExerciseItem(3L, "Decline Barbell Press"),
            new ExerciseItem(4L, "Dumbbell Fly"),
            new ExerciseItem(5L, "Pec Deck Machine")
        ));
        library.put("back", Arrays.asList(
            new ExerciseItem(6L, "Pull-up"),
            new ExerciseItem(7L, "Barbell Row"),
            new ExerciseItem(8L, "Dumbbell Row"),
            new ExerciseItem(9L, "Seated Cable Row"),
            new ExerciseItem(10L, "Lat Pulldown")
        ));
        library.put("shoulder", Arrays.asList(
            new ExerciseItem(11L, "Dumbbell Shoulder Press"),
            new ExerciseItem(12L, "Lateral Raise"),
            new ExerciseItem(13L, "Front Raise"),
            new ExerciseItem(14L, "Bent-over Reverse Fly"),
            new ExerciseItem(15L, "Shrug")
        ));
        library.put("legs", Arrays.asList(
            new ExerciseItem(16L, "Barbell Squat"),
            new ExerciseItem(17L, "Leg Press"),
            new ExerciseItem(18L, "Leg Curl"),
            new ExerciseItem(19L, "Leg Extension"),
            new ExerciseItem(20L, "Deadlift")
        ));
        library.put("arms", Arrays.asList(
            new ExerciseItem(21L, "Barbell Curl"),
            new ExerciseItem(22L, "Dumbbell Curl"),
            new ExerciseItem(23L, "Close Grip Bench Press"),
            new ExerciseItem(24L, "Cable Pushdown"),
            new ExerciseItem(25L, "Hammer Curl")
        ));
    }

    public static Map<String, List<ExerciseItem>> getLibrary() {
        return library;
    }

    public static List<ExerciseItem> getByCategory(String category) {
        return library.getOrDefault(category, Collections.emptyList());
    }

    public static class ExerciseItem {
        private Long id;
        private String name;

        public ExerciseItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}