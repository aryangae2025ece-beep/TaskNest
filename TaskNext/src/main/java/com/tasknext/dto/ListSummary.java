package com.tasknext.dto;

/** A list plus its completion numbers, shown in the sidebar. */
public class ListSummary {
    private final Long id;
    private final String name;
    private final String color;
    private final long total;
    private final long done;

    public ListSummary(Long id, String name, String color, long total, long done) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.total = total;
        this.done = done;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }
    public long getTotal() { return total; }
    public long getDone() { return done; }
    public long getOpen() { return total - done; }
}
