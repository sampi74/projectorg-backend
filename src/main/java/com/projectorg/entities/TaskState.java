package com.projectorg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "task_state")
public class TaskState {

    // atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskStateId;

    @Column(name = "taskStateName", nullable = false, unique = true)
    private String taskStateName;

    @Column(name = "lowDateTaskState")
    private LocalDate lowDateTaskState;

    @OneToMany(mappedBy = "state")
    @ToString.Exclude
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

    public TaskState(String taskStateName) {
        this.taskStateName = taskStateName;
    }

}
