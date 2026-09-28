package com.tasknext.repository;

import com.tasknext.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    List<TaskList> findAllByOrderByNameAsc();
}
