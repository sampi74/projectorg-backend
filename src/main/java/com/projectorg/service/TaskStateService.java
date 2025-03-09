package com.projectorg.service;

import com.projectorg.entities.DiagramType;
import com.projectorg.entities.TaskState;
import com.projectorg.repository.TaskStateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskStateService extends BaseService<TaskState, Long> {
    private final TaskStateRepository taskStateRepository;
    public TaskStateService(TaskStateRepository taskStateRepository) {
        super(taskStateRepository);
        this.taskStateRepository = taskStateRepository;
    }

    public void create(String name){
        if (taskStateRepository.existsByTaskStateName(name)){
            throw new IllegalArgumentException("The name " + name + " is already in use.");
        }
        TaskState taskState = new TaskState(name);
        taskStateRepository.save(taskState);
    }

    public void update(Long tsId, String name) {
        TaskState taskState = taskStateRepository.findById(tsId)
                .orElseThrow(() -> new EntityNotFoundException("Task State not found"));

        if (taskStateRepository.existsByTaskStateName(name)){
            throw new IllegalArgumentException("The name " + name + " is already in use.");
        }

        taskState.setTaskStateName(name);
        taskStateRepository.save(taskState);
    }

    public void drop(Long tsId) {
        TaskState taskState = taskStateRepository.findById(tsId)
                .orElseThrow(() -> new EntityNotFoundException("Task State not found"));

        taskState.setLowDateTaskState(LocalDate.now());
        taskStateRepository.save(taskState);
    }

    public void discharge(Long tsId) {
        TaskState taskState = taskStateRepository.findById(tsId)
                .orElseThrow(() -> new EntityNotFoundException("Task State not found"));

        if (taskState.getLowDateTaskState() == null){
            throw new IllegalStateException("Low date task status should not be null");
        }

        taskState.setLowDateTaskState(null);
        taskStateRepository.save(taskState);
    }

    public List<TaskState> getAll() {
        List<TaskState> taskStates = taskStateRepository.findByLowDateTaskStateIsNull();
        return taskStates;
    }

    public TaskState get(Long tsId) {
        TaskState taskState = taskStateRepository.findById(tsId)
                .orElseThrow(() -> new EntityNotFoundException("Task State not found"));
        return taskState;
    }

    public List<TaskState> getAllLow() {
        List<TaskState> taskStates = taskStateRepository.findByLowDateTaskStateIsNotNull();
        return taskStates;
    }
}
