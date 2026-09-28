package com.tasknext.dto;

/** Completion tracking numbers for a set of tasks. */
public class TaskStats {
    private final long total;
    private final long done;
    private final long overdue;
    private final long dueToday;

    public TaskStats(long total, long done, long overdue, long dueToday) {
        this.total = total;
        this.done = done;
        this.overdue = overdue;
        this.dueToday = dueToday;
    }

    public long getTotal() { return total; }
    public long getDone() { return done; }
    public long getOpen() { return total - done; }
    public long getOverdue() { return overdue; }
    public long getDueToday() { return dueToday; }

    public int getPercent() {
        return total == 0 ? 0 : (int) Math.round(done * 100.0 / total);
    }
}
