package com.projectorg.dto;

import lombok.Data;

@Data
public class TaskDto {
    private Long taskId;
    private String taskText;
    private Long state;
}
