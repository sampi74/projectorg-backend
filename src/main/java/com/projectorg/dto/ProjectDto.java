package com.projectorg.dto;

import com.projectorg.enumerations.ProjectState;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectDto {

    private String projectName;
    private String projectDescription;
    private LocalDate projectUpDate;
    private Long leaderId;
    private ProjectState state;

}
