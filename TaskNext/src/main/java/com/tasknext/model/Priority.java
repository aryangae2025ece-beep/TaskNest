package com.tasknext.model;

import java.util.Locale;

public enum Priority {
    HIGH("High"),
    MEDIUM("Medium"),
    LOW("Low");

    private final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** Lower-case name used as a CSS class suffix. */
    public String getCss() {
        return name().toLowerCase(Locale.ROOT);
    }
}
