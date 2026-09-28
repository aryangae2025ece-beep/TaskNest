package com.tasknext.config;

import com.tasknext.service.TaskListService;
import com.tasknext.service.TaskService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/** Adds the sidebar data (lists and counts) to every page. */
@ControllerAdvice
public class GlobalModelAdvice {

    private final TaskListService listService;
    private final TaskService taskService;

    public GlobalModelAdvice(TaskListService listService, TaskService taskService) {
        this.listService = listService;
        this.taskService = taskService;
    }

    @ModelAttribute
    public void addSidebarData(Model model) {
        model.addAttribute("navLists", listService.summaries());
        model.addAttribute("navCounts", taskService.navCounts());
    }
}
