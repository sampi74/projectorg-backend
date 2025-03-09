package com.projectorg.repository;

import com.projectorg.entities.DiagramType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagramTypeRepository extends JpaRepository<DiagramType, Long>{
    DiagramType findByDiagramTypeName(String diagramTypeName);

    boolean existsByDiagramTypeName(String diagramTypeName);

    List<DiagramType> findByLowDateDiagramTypeIsNull();

    List<DiagramType> findByLowDateDiagramTypeIsNotNull();
}
