package com.projectorg.service;

import com.projectorg.entities.DiagramType;
import com.projectorg.repository.DiagramTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiagramTypeService extends BaseService<DiagramType, Long> {
    private final DiagramTypeRepository diagramTypeRepository;
    public DiagramTypeService(DiagramTypeRepository diagramTypeRepository) {
        super(diagramTypeRepository);
        this.diagramTypeRepository = diagramTypeRepository;
    }

    public void create(String name){
        if (diagramTypeRepository.existsByDiagramTypeName(name)){
            throw new IllegalArgumentException("The name " + name + " is already in use.");
        }
        DiagramType diagramType = new DiagramType(name);
        diagramTypeRepository.save(diagramType);
    }

    public void update(Long dtId, String name) {
        DiagramType diagramType = diagramTypeRepository.findById(dtId)
                .orElseThrow(() -> new EntityNotFoundException("Diagram Type not found"));

        if (diagramTypeRepository.existsByDiagramTypeName(name)){
            throw new IllegalArgumentException("The name " + name + " is already in use.");
        }

        diagramType.setDiagramTypeName(name);
        diagramTypeRepository.save(diagramType);
    }

    public void drop(Long dtId) {
        DiagramType diagramType = diagramTypeRepository.findById(dtId)
                .orElseThrow(() -> new EntityNotFoundException("Diagram Type not found"));

        diagramType.setLowDateDiagramType(LocalDate.now());
        diagramTypeRepository.save(diagramType);
    }

    public void discharge(Long dtId) {
        DiagramType diagramType = diagramTypeRepository.findById(dtId)
                .orElseThrow(() -> new EntityNotFoundException("Diagram Type not found"));

        if (diagramType.getLowDateDiagramType() == null){
            throw new IllegalStateException("Diagram type low date should not be null");
        }

        diagramType.setLowDateDiagramType(null);
        diagramTypeRepository.save(diagramType);
    }

    public List<DiagramType> getAll() {
        List<DiagramType> diagramTypes = diagramTypeRepository.findByLowDateDiagramTypeIsNull();
        return diagramTypes;
    }

    public List<DiagramType> getAllLow() {
        List<DiagramType> diagramTypes = diagramTypeRepository.findByLowDateDiagramTypeIsNotNull();
        return diagramTypes;
    }

    public DiagramType get(Long dtId) {
        DiagramType diagramType = diagramTypeRepository.findById(dtId)
                .orElseThrow(() -> new EntityNotFoundException("Diagram Type not found"));
        return diagramType;
    }
}
