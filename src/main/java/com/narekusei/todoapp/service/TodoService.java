package com.narekusei.todoapp.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.narekusei.todoapp.model.TodoItem;

@Service // Tells Spring that this is service component to manage

public class TodoService {
    // TO keep track of our tasks. For now, an in-memory list.
    // CopyOnWriteArrayList is good if multiple people (or threads) might change the list at once.
    private final List<TodoItem> todos = new CopyOnWriteArrayList<>();

    // Let's add a few default tasks so the list isn't empty when we start.
    public TodoService() {
        todos.add(new TodoItem("Learn Spring Boot"));
        todos.add(new TodoItem("Build a To-Do App"));
        TodoItem completedTask = new TodoItem("Push to GitHub");
        completedTask.setCompleted(true);
        completedTask.setLastModified(LocalDateTime.now().minusDays(1)); // Pretend this was done yesterday
        todos.add(completedTask);
    }

    // Method to get ALL the to-do items. The controller will use this to show them.
    public List<TodoItem> getAllTodos() {
        // Just return the whole list. Simple!
        return todos;
    }

    // Method to add a NEW to-do item.
    public void addTodo(String taskDescription) {
        if (taskDescription != null && !taskDescription.trim().isEmpty()) {
            TodoItem newItem = new TodoItem(taskDescription);
            todos.add(newItem);
            // No need to call newItem.updateLastModified() here because the constructor sets it.
            System.out.println("Added new task: " + taskDescription); // A little log for the console
        } else {
            System.out.println("Tried to add an empty task. Ignoring.");
        }
    }

    // Method to find a specific to-do item by its ID.
    // Optional is a nice way to handle cases where the item might not exist.
    public Optional<TodoItem> getTodoById(String id) {
        return todos.stream()
                .filter(todo -> todo.getId().equals(id))
                .findFirst();
    }

    // Method to mark a task as completed or not completed (toggle).
    public void toggleTodoCompletion(String id) {
        getTodoById(id).ifPresent(todo -> { // ifPresent is a cool Optional method
            todo.setCompleted(!todo.isCompleted()); // Flip the completed status
            todo.updateLastModified(); // And update the timestamp!
            System.out.println("Toggled completion for task ID: " + id);
        });
    }

    // Method to DELETE a to-do item.
    public void deleteTodo(String id) {
        // removeIf is a handy way to remove items from a list based on a condition.
        boolean removed = todos.removeIf(todo -> todo.getId().equals(id));
        if (removed) {
            System.out.println("Deleted task ID: " + id);
        } else {
            System.out.println("Could not find task ID to delete: " + id);
        }
    }

    // Method to update an existing task's description.
    public void updateTaskDescription(String id, String newDescription) {
        getTodoById(id).ifPresent(todo -> {
            if (newDescription != null && !newDescription.trim().isEmpty()) {
                todo.setTask(newDescription);
                todo.updateLastModified();
                System.out.println("Updated task ID: " + id + " with new description: " + newDescription);
            }
        });
    }

    // --- NEW METHOD FOR SORTING ---
    /**
     * Sorts the internal list of To-Do items.
     * The primary sort key is the completion status (incomplete tasks first).
     * The secondary sort key is the 'lastModified' timestamp (most recent first within each group).
     * This method modifies the order of items in the 'todos' list directly.
     */

    public void sortTodosByCompletionAndDate() {
        // Comparator for sorting:
        // 1. By completion status (false/incomplete before true/completed).
        // 2. Then, by lastModified date in descending order (newest first).
        Comparator<TodoItem> comparator = Comparator
                .comparing(TodoItem::isCompleted) // false comes before true by default
                .thenComparing(TodoItem::getLastModified, Comparator.reverseOrder());

        // The 'sort' method of List directly modifies the list.
        // For CopyOnWriteArrayList, this operation creates a new underlying array.
        this.todos.sort(comparator);
        System.out.println("Service: To-Do items have been sorted by completion status and modification date.");
    }
}
