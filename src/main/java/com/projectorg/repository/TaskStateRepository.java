package com.projectorg.repository;

import com.projectorg.entities.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStateRepository extends JpaRepository<TaskState, Long>{
    public TaskState findByTaskStateName(String taskStateName);
    public Boolean existsByTaskStateName(String taskStateName);
    List<TaskState> findByLowDateTaskStateIsNull();
    List<TaskState> findByLowDateTaskStateIsNotNull();
}
