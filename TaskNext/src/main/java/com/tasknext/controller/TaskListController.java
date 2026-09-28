package com.tasknext.controller;
import org.springframework.stereotype.Controller;

import com.tasknext.dto.ListForm;
import com.tasknext.service.TaskListService;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TaskListController {

    private static final List<String> PALETTE = List.of(
            "#3450D6", "#8A5CC2", "#2E9E83", "#D9455F",
            "#E08A1E", "#1B8FB8", "#6B7A2E", "#B8467F");

    private final TaskListService listService;

    public TaskListController(TaskListService listService) {
        this.listService = listService;
    }

    @GetMapping("/lists/new")
    public String newList(Model model) {
        prepare(model, new ListForm(), "New list");
        return "list-form";
    }

    @GetMapping("/lists/{id}/edit")
    public String editList(@PathVariable Long id, Model model) {
        prepare(model, ListForm.from(listService.get(id)), "Edit list");
        return "list-form";
    }

    @PostMapping("/lists")
    public String save(@Valid @ModelAttribute("listForm") ListForm form,
                       BindingResult result, Model model) {
        if (result.hasErrors()) {
            prepare(model, null, form.getId() == null ? "New list" : "Edit list");
            return "list-form";
        }
        Long id = listService.save(form).getId();
        return "redirect:/?list=" + id;
    }

    @PostMapping("/lists/{id}/delete")
    public String delete(@PathVariable Long id) {
        listService.delete(id);
        return "redirect:/";
    }

    private void prepare(Model model, ListForm form, String heading) {
        if (form != null) {
            model.addAttribute("listForm", form);
        }
        model.addAttribute("palette", PALETTE);
        model.addAttribute("heading", heading);
        model.addAttribute("activeView", null);
    }
}
