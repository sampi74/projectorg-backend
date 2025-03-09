package com.projectorg.entities;

import com.projectorg.enumerations.RequirementType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "requirements")
public class Requirement {

    // atributo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requirementId;

    @Column(name = "requirementText", nullable = false)
    private String requirementText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequirementType requirementType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}
