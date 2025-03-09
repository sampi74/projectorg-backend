package com.projectorg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projectorg.enumerations.ProjectState;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@Table(name = "projects")
public class Project {

    // atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, name = "projectName")
    private String projectName;

    @Column(nullable = false, name = "projectDescription")
    private String projectDescription;

    @Column(name = "upDateProject", nullable = false)
    private LocalDate upDateProject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "state")
    private ProjectState state;

    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "leaderId", nullable = false)
    private User projectLeader;

    @OneToMany(mappedBy = "project")
    @JsonIgnore
    @ToString.Exclude
    private Set<ProjectCollaborator> collaborators;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Task> tasks;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Diagram> diagrams;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Requirement> requirements;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Idea> ideas;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<File> files;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(projectId, project.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId);
    }

}
