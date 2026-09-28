package com.tasknext.repository;

import com.tasknext.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select t from Task t join fetch t.taskList order by t.id")
    List<Task> findAllWithList();

    @Query("select t from Task t join fetch t.taskList where t.taskList.id = :listId order by t.id")
    List<Task> findByListWithList(@Param("listId") Long listId);

    /** Rows of [listId, totalTasks, completedTasks]. */
    @Query("select t.taskList.id, count(t), sum(case when t.completed = true then 1 else 0 end) "
            + "from Task t group by t.taskList.id")
    List<Object[]> countByList();

    @Modifying
    @Query("delete from Task t where t.taskList.id = :listId")
    void deleteAllByList(@Param("listId") Long listId);
}
