package com.tasknext.dto;

import com.tasknext.model.Priority;
import com.tasknext.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class TaskForm {

    private Long id;

    @NotBlank(message = "Give the task a title.")
    @Size(max = 150, message = "Keep the title under 150 characters.")
    private String title;

    @Size(max = 1000, message = "Keep the notes under 1000 characters.")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    @NotNull(message = "Choose a priority.")
    private Priority priority = Priority.MEDIUM;

    @NotNull(message = "Choose a list for this task.")
    private Long listId;

    public static TaskForm from(Task task) {
        TaskForm f = new TaskForm();
        f.id = task.getId();
        f.title = task.getTitle();
        f.description = task.getDescription();
        f.dueDate = task.getDueDate();
        f.priority = task.getPriority();
        f.listId = task.getTaskList().getId();
        return f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Long getListId() { return listId; }
    public void setListId(Long listId) { this.listId = listId; }
}
