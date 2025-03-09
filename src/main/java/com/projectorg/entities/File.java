package com.projectorg.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@Table(name = "files")
public class File {

    // archivos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(name = "fileName", nullable = false)
    private String fileName;

    @Column(name = "fileImageUrl", nullable = false)
    private String fileImageUrl;

    @Column(name = "lowDateFile")
    private LocalDate lowDateFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId", nullable = false)
    private Project project;

}
