package com.fitnessapp.model;

// All by myself
// Workout session request DTO
public class WorkoutSessionRequest {
    private String username;
    private String duration;
    private int totalSets;
    private double totalVolume;
    private int totalReps;
    private int prCount;
    private long startTime;
    private long endTime;

    // Constructors
    public WorkoutSessionRequest() {}

    public WorkoutSessionRequest(String username, String duration, int totalSets, 
                               double totalVolume, int totalReps, int prCount, 
                               long startTime, long endTime) {
        this.username = username;
        this.duration = duration;
        this.totalSets = totalSets;
        this.totalVolume = totalVolume;
        this.totalReps = totalReps;
        this.prCount = prCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(int totalSets) {
        this.totalSets = totalSets;
    }

    public double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public int getTotalReps() {
        return totalReps;
    }

    public void setTotalReps(int totalReps) {
        this.totalReps = totalReps;
    }

    public int getPrCount() {
        return prCount;
    }

    public void setPrCount(int prCount) {
        this.prCount = prCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
} 