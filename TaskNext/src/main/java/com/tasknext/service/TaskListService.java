package com.tasknext.service;

import com.tasknext.dto.ListForm;
import com.tasknext.dto.ListSummary;
import com.tasknext.model.TaskList;
import com.tasknext.repository.TaskListRepository;
import com.tasknext.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TaskListService {

    private final TaskListRepository listRepo;
    private final TaskRepository taskRepo;

    public TaskListService(TaskListRepository listRepo, TaskRepository taskRepo) {
        this.listRepo = listRepo;
        this.taskRepo = taskRepo;
    }

    @Transactional(readOnly = true)
    public List<TaskList> findAll() {
        return listRepo.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public TaskList get(Long id) {
        return listRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "That list doesn't exist."));
    }

    public TaskList save(ListForm form) {
        TaskList list = form.getId() == null ? new TaskList() : get(form.getId());
        list.setName(form.getName().trim());
        String desc = form.getDescription();
        list.setDescription(desc == null || desc.isBlank() ? null : desc.trim());
        list.setColor(form.getColor());
        return listRepo.save(list);
    }

    /** Deletes the list and every task inside it. */
    public void delete(Long id) {
        TaskList list = get(id);
        taskRepo.deleteAllByList(id);
        listRepo.delete(list);
    }

    @Transactional(readOnly = true)
    public List<ListSummary> summaries() {
        Map<Long, long[]> counts = new HashMap<>();
        for (Object[] row : taskRepo.countByList()) {
            counts.put(((Number) row[0]).longValue(),
                    new long[]{((Number) row[1]).longValue(), row[2] == null ? 0 : ((Number) row[2]).longValue()});
        }
        return listRepo.findAllByOrderByNameAsc().stream()
                .map(l -> {
                    long[] c = counts.getOrDefault(l.getId(), new long[]{0, 0});
                    return new ListSummary(l.getId(), l.getName(), l.getColor(), c[0], c[1]);
                })
                .toList();
    }
}
