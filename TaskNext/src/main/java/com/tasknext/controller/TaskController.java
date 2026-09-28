package com.tasknext.controller;
import org.springframework.stereotype.Controller;

import com.tasknext.dto.TaskForm;
import com.tasknext.model.Priority;
import com.tasknext.model.Task;
import com.tasknext.model.TaskList;
import com.tasknext.service.TaskListService;
import com.tasknext.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Locale;
import java.util.Map;

@Controller
public class TaskController {

    private static final Map<String, String[]> VIEW_TEXT = Map.of(
            "all", new String[]{"All tasks", "Everything across your lists."},
            "open", new String[]{"Open tasks", "Tasks you haven't finished yet."},
            "today", new String[]{"Due today", "Tasks that need to be done today."},
            "upcoming", new String[]{"Upcoming", "Tasks due after today."},
            "overdue", new String[]{"Overdue", "Tasks that are past their due date."},
            "completed", new String[]{"Completed", "Everything you've finished."}
    );

    private final TaskService taskService;
    private final TaskListService listService;

    public TaskController(TaskService taskService, TaskListService listService) {
        this.taskService = taskService;
        this.listService = listService;
    }

    @GetMapping("/")
    public String home(@RequestParam(name = "list", required = false) Long listId,
                       @RequestParam(name = "view", defaultValue = "all") String view,
                       @RequestParam(name = "priority", required = false) Priority priority,
                       @RequestParam(name = "q", required = false) String q,
                       Model model) {
        String v = view.toLowerCase(Locale.ROOT);
        if (!VIEW_TEXT.containsKey(v)) {
            v = "all";
        }
        TaskList active = listId == null ? null : listService.get(listId);

        String title = active != null ? active.getName() : VIEW_TEXT.get(v)[0];
        String subtitle = active != null
                ? (active.getDescription() == null ? "" : active.getDescription())
                : VIEW_TEXT.get(v)[1];

        model.addAttribute("tasks", taskService.find(listId, v, priority, q));
        model.addAttribute("stats", taskService.stats(listId));
        model.addAttribute("activeList", active);
        model.addAttribute("activeListId", listId);
        model.addAttribute("activeView", active == null ? v : null);
        model.addAttribute("view", v);
        model.addAttribute("priority", priority);
        model.addAttribute("q", q);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("filtersActive", priority != null || StringUtils.hasText(q) || !v.equals("all"));
        model.addAttribute("pageTitle", title);
        model.addAttribute("pageSubtitle", subtitle);
        model.addAttribute("returnTo", buildReturn(listId, v, priority, q));
        return "tasks";
    }

    @GetMapping("/tasks/new")
    public String newTask(@RequestParam(name = "list", required = false) Long listId, Model model) {
        if (listService.findAll().isEmpty()) {
            return "redirect:/lists/new";
        }
        TaskForm form = new TaskForm();
        form.setListId(listId != null ? listId : listService.findAll().get(0).getId());
        prepareForm(model, form, "Add task");
        return "task-form";
    }

    @GetMapping("/tasks/{id}/edit")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskService.get(id);
        prepareForm(model, TaskForm.from(task), "Edit task");
        return "task-form";
    }

    @PostMapping("/tasks")
    public String save(@Valid @ModelAttribute("taskForm") TaskForm form,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            prepareForm(model, null, form.getId() == null ? "Add task" : "Edit task");
            return "task-form";
        }
        Task saved = taskService.save(form);
        return "redirect:/?list=" + saved.getTaskList().getId();
    }

    @PostMapping("/tasks/{id}/toggle")
    public String toggle(@PathVariable Long id,
                         @RequestParam(name = "returnTo", required = false) String returnTo) {
        taskService.toggle(id);
        return "redirect:" + safeReturn(returnTo);
    }

    @PostMapping("/tasks/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(name = "returnTo", required = false) String returnTo) {
        taskService.delete(id);
        return "redirect:" + safeReturn(returnTo);
    }

    // ---- helpers ----

    private void prepareForm(Model model, TaskForm form, String heading) {
        if (form != null) {
            model.addAttribute("taskForm", form);
        }
        model.addAttribute("allLists", listService.findAll());
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("heading", heading);
        model.addAttribute("activeView", null);
    }

    private String buildReturn(Long listId, String view, Priority priority, String q) {
        UriComponentsBuilder b = UriComponentsBuilder.fromPath("/");
        if (listId != null) b.queryParam("list", listId);
        if (view != null && !view.equals("all")) b.queryParam("view", view);
        if (priority != null) b.queryParam("priority", priority.name());
        if (StringUtils.hasText(q)) b.queryParam("q", q);
        return b.build().encode().toUriString();
    }

    /** Only allow redirects back to a path inside this app. */
    static String safeReturn(String target) {
        if (target != null && target.startsWith("/") && !target.startsWith("//")
                && !target.contains("\\") && !target.contains("\r") && !target.contains("\n")) {
            return target;
        }
        return "/";
    }
}
