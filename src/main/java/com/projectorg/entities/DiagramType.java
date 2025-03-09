package com.projectorg.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "diagram_types")
public class DiagramType {

    // atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diagramTypeId;

    @Column(name = "diagramTypeName", nullable = false, unique = true)
    private String diagramTypeName;

    @Column(name = "lowDateDiagramType")
    private LocalDate lowDateDiagramType;

    @OneToMany(mappedBy = "diagramType")
    @ToString.Exclude
    @JsonIgnore
    private List<Diagram> diagrams = new ArrayList<>();

    public DiagramType(String diagramTypeName) {
        this.diagramTypeName = diagramTypeName;
    }

}
