package com.narekusei.todoapp.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.narekusei.todoapp.model.TodoItem;
import com.narekusei.todoapp.service.TodoService;

@Controller // Marks this as a Spring MVC Controller. It handles web traffic.

public class TodoController {
    // We need our TodoService to do the actual work.
    // @Autowired tells Spring to "inject" an instance of TodoService here.
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // This method handles requests to the main page ("/").
    @GetMapping("/")
    public String index(Model model) { // Model carrys data to the HTML page.
        List<TodoItem> todos = todoService.getAllTodos();
        // Let's sort them: incomplete first, then by last modified (newest first)
        todos.sort(Comparator.comparing(TodoItem::isCompleted) // false (incomplete) comes before true (completed)
                             .thenComparing(TodoItem::getLastModified, Comparator.reverseOrder()));
        model.addAttribute("todos", todos); // Add the list of todos to the "Model"
        model.addAttribute("newTodo", new TodoItem("")); // For the form to add a new task
        return "index"; // Tells Spring to use the "index.html" template.
    }

    // This method handles form submissions to add a new task.
    // It listens for POST requests to "/todo/add".
    @PostMapping("/todo/add")
    public String addTodo(@RequestParam("task") String taskDescription, RedirectAttributes redirectAttributes) {
        // @RequestParam("task") grabs the 'task' value from the form.
        if (taskDescription != null && !taskDescription.trim().isEmpty()) {
            todoService.addTodo(taskDescription);
            redirectAttributes.addFlashAttribute("successMessage", "Task added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Task description cannot be empty.");
        }
        return "redirect:/"; // After adding, redirect back to the main page.
    }

    // This method handles requests to toggle a task's completion status.
    // Example URL: /todo/toggle/some-uuid-string
    @GetMapping("/todo/toggle/{id}")
    public String toggleTodo(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        // @PathVariable("id") gets the 'id' from the URL path.
        todoService.toggleTodoCompletion(id);
        redirectAttributes.addFlashAttribute("infoMessage", "Task status updated!");
        return "redirect:/"; // And redirect back.
    }

    // This method handles requests to delete a task.
    // Example URL: /todo/delete/some-uuid-string
    @GetMapping("/todo/delete/{id}")
    public String deleteTodo(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        todoService.deleteTodo(id);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        return "redirect:/"; // redirect back.
    }

    // Show the form to edit a task
    @GetMapping("/todo/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        return todoService.getTodoById(id)
            .map(todo -> {
                model.addAttribute("todoToEdit", todo);
                return "edit-todo"; // Name of the Thymeleaf template for editing
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("errorMessage", "Task not found for editing.");
                return "redirect:/";
            });
    }

    // Process the edit form submission
    @PostMapping("/todo/update")
    public String updateTodo(@RequestParam("id") String id,
                             @RequestParam("task") String taskDescription,
                             RedirectAttributes redirectAttributes) {
        if (taskDescription != null && !taskDescription.trim().isEmpty()) {
            todoService.updateTaskDescription(id, taskDescription);
            redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Task description cannot be empty for update.");
        }
        return "redirect:/";
    }
}
