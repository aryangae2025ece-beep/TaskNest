package com.tasknext.dto;

import com.tasknext.model.TaskList;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ListForm {

    private Long id;

    @NotBlank(message = "Give the list a name.")
    @Size(max = 60, message = "Keep the name under 60 characters.")
    private String name;

    @Size(max = 255, message = "Keep the description under 255 characters.")
    private String description;

    @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Pick a colour for the list.")
    private String color = "#3450D6";

    public static ListForm from(TaskList list) {
        ListForm f = new ListForm();
        f.id = list.getId();
        f.name = list.getName();
        f.description = list.getDescription();
        f.color = list.getColor();
        return f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
