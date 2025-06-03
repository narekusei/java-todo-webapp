// this is where we define what a to-do item actually IS.
package com.narekusei.todoapp.model;


import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Generates a no-argument constructor

public class TodoItem {
    // Each to-do needs a unique ID. So we can find it later.
    // Using UUID to make sure it's really unique, even if we had a database later.
    private String id;

    // This is the actual task description, like "Buy milk"
    private String task;

     // Is it done? Or still pending?
    private boolean completed;

     // When was this task last touched?
    private LocalDateTime lastModified;

    // This is a constructor. It's like a blueprint for creating new TodoItem objects.
    // We'll call this when a user adds a new task.
    public TodoItem(String task) {
        this.id = UUID.randomUUID().toString(); // Give it a random unique ID
        this.task = task;
        this.completed = false; // New tasks are not completed by default.
        this.lastModified = LocalDateTime.now(); // Set the current time as last modified
    }

    // Another constructor for loading from a database or for other specific cases.
    public TodoItem(String id, String task, boolean completed, LocalDateTime lastModified) {
        this.id = id;
        this.task = task;
        this.completed = completed;
        this.lastModified = lastModified;
    }

     // Just in case we need to update the last modified time explicitly.
    public void updateLastModified() {
        this.lastModified = LocalDateTime.now();
    }
}
