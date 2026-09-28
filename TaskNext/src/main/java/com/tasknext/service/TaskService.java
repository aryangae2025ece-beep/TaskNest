package com.tasknext.service;

import com.tasknext.dto.TaskForm;
import com.tasknext.dto.TaskStats;
import com.tasknext.model.Priority;
import com.tasknext.model.Task;
import com.tasknext.model.TaskList;
import com.tasknext.repository.TaskListRepository;
import com.tasknext.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepo;
    private final TaskListRepository listRepo;

    public TaskService(TaskRepository taskRepo, TaskListRepository listRepo) {
        this.taskRepo = taskRepo;
        this.listRepo = listRepo;
    }

    /** Open tasks first, then by due date (no date last), then by priority, then oldest first. */
    private static final Comparator<Task> ORDER = Comparator
            .comparing(Task::isCompleted)
            .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getPriority)
            .thenComparing(Task::getId);

    @Transactional(readOnly = true)
    public List<Task> find(Long listId, String view, Priority priority, String query) {
        LocalDate today = LocalDate.now();
        String v = view == null ? "all" : view.toLowerCase(Locale.ROOT);

        Predicate<Task> byView = switch (v) {
            case "open" -> t -> !t.isCompleted();
            case "today" -> Task::isDueToday;
            case "upcoming" -> t -> !t.isCompleted() && t.getDueDate() != null && t.getDueDate().isAfter(today);
            case "overdue" -> Task::isOverdue;
            case "completed" -> Task::isCompleted;
            default -> t -> true;
        };
        Predicate<Task> byPriority = t -> priority == null || t.getPriority() == priority;
        String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        Predicate<Task> byText = t -> needle.isEmpty()
                || t.getTitle().toLowerCase(Locale.ROOT).contains(needle)
                || (t.getDescription() != null && t.getDescription().toLowerCase(Locale.ROOT).contains(needle));

        return load(listId).stream()
                .filter(byView).filter(byPriority).filter(byText)
                .sorted(ORDER)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskStats stats(Long listId) {
        List<Task> tasks = load(listId);
        long done = tasks.stream().filter(Task::isCompleted).count();
        long overdue = tasks.stream().filter(Task::isOverdue).count();
        long today = tasks.stream().filter(Task::isDueToday).count();
        return new TaskStats(tasks.size(), done, overdue, today);
    }

    /** Numbers shown beside the sidebar links. */
    @Transactional(readOnly = true)
    public Map<String, Long> navCounts() {
        LocalDate today = LocalDate.now();
        List<Task> all = taskRepo.findAllWithList();
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("open", all.stream().filter(t -> !t.isCompleted()).count());
        counts.put("today", all.stream().filter(Task::isDueToday).count());
        counts.put("upcoming", all.stream()
                .filter(t -> !t.isCompleted() && t.getDueDate() != null && t.getDueDate().isAfter(today)).count());
        counts.put("overdue", all.stream().filter(Task::isOverdue).count());
        counts.put("completed", all.stream().filter(Task::isCompleted).count());
        return counts;
    }

    @Transactional(readOnly = true)
    public Task get(Long id) {
        return taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "That task doesn't exist."));
    }

    public Task save(TaskForm form) {
        Task task = form.getId() == null ? new Task() : get(form.getId());
        TaskList list = listRepo.findById(form.getListId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "That list doesn't exist."));
        task.setTitle(form.getTitle().trim());
        String desc = form.getDescription();
        task.setDescription(desc == null || desc.isBlank() ? null : desc.trim());
        task.setDueDate(form.getDueDate());
        task.setPriority(form.getPriority());
        task.setTaskList(list);
        return taskRepo.save(task);
    }

    public void toggle(Long id) {
        Task task = get(id);
        boolean nowDone = !task.isCompleted();
        task.setCompleted(nowDone);
        task.setCompletedAt(nowDone ? LocalDateTime.now() : null);
        taskRepo.save(task);
    }

    public void delete(Long id) {
        taskRepo.delete(get(id));
    }

    private List<Task> load(Long listId) {
        return listId == null ? taskRepo.findAllWithList() : taskRepo.findByListWithList(listId);
    }
}
