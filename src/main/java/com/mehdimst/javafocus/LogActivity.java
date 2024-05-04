package com.mehdimst.javafocus;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class LogActivity {

    // Set counter static for generate log activity identifier
    private static int _counter = 0;

    // Private fields
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;

    // Constructor
    public LogActivity(LocalDateTime startTime) {
        setId(++_counter);
        setStartTime(startTime);
    }

    // Getters and setters

    private void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    private void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Compare class
    static class Comparator implements java.util.Comparator<LogActivity> {
        @Override
        public int compare(LogActivity object1, LogActivity object2) {
            return object2.getStartTime().compareTo(object1.getStartTime());
        }
    }
}