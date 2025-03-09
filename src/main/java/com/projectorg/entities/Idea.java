package com.projectorg.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ideas")
public class Idea {

    // atributo
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ideaId;

    @Column(name = "ideaText", nullable = false)
    private String ideaText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

}
