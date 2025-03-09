package com.projectorg.repository;

import com.projectorg.entities.Project;
import com.projectorg.enumerations.ProjectState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    List<Project> findAllByState(ProjectState state);

}
