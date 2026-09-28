package com.tasknext.config;

import com.tasknext.model.Priority;
import com.tasknext.model.Task;
import com.tasknext.model.TaskList;
import com.tasknext.repository.TaskListRepository;
import com.tasknext.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Creates three starter lists and a few sample tasks the first time the app runs. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskListRepository listRepo;
    private final TaskRepository taskRepo;

    public DataSeeder(TaskListRepository listRepo, TaskRepository taskRepo) {
        this.listRepo = listRepo;
        this.taskRepo = taskRepo;
    }

    @Override
    public void run(String... args) {
        if (listRepo.count() > 0) {
            return;
        }
        TaskList assignments = list("Assignments", "Coursework and deadlines", "#3450D6");
        TaskList club = list("Club Work", "Events, meetings and volunteering", "#8A5CC2");
        TaskList errands = list("Personal Errands", "Life outside class", "#2E9E83");

        LocalDate today = LocalDate.now();
        task(assignments, "Submit database systems report", "ER diagram and normalisation to 3NF.",
                today.plusDays(2), Priority.HIGH, false);
        task(assignments, "Read chapter 5 for operating systems", null,
                today.plusDays(5), Priority.MEDIUM, false);
        task(assignments, "Finish maths problem set", "Questions 1 to 12.",
                today.minusDays(1), Priority.HIGH, false);
        task(assignments, "Register for lab session", null,
                today.minusDays(3), Priority.LOW, true);
        task(club, "Book the seminar hall for Friday", "Confirm projector and mics.",
                today, Priority.HIGH, false);
        task(club, "Send meeting notes to the team", null,
                today.plusDays(1), Priority.MEDIUM, false);
        task(errands, "Collect printed project copies", null,
                today.plusDays(3), Priority.LOW, false);
        task(errands, "Pay hostel fees", "Keep the receipt.",
                today.plusDays(8), Priority.MEDIUM, false);
    }

    private TaskList list(String name, String description, String color) {
        TaskList l = new TaskList();
        l.setName(name);
        l.setDescription(description);
        l.setColor(color);
        return listRepo.save(l);
    }

    private void task(TaskList list, String title, String description, LocalDate due,
                      Priority priority, boolean done) {
        Task t = new Task();
        t.setTaskList(list);
        t.setTitle(title);
        t.setDescription(description);
        t.setDueDate(due);
        t.setPriority(priority);
        t.setCompleted(done);
        t.setCompletedAt(done ? LocalDateTime.now() : null);
        taskRepo.save(t);
    }
}
