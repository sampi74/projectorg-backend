package com.projectorg.repository;

import com.projectorg.entities.Project;
import com.projectorg.entities.ProjectCollaborator;
import com.projectorg.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectCollaboratorRepository extends JpaRepository<ProjectCollaborator, Long> {

    List<ProjectCollaborator> findByProject(Project project);

    List<ProjectCollaborator> findByUser(User user);

    boolean existsByProjectAndUser(Project project, User user);

    void deleteByProjectAndUser(Project project, User user);

    void deleteByProject(Project project);

    Optional<ProjectCollaborator> findByProjectAndUser(Project project, Optional<User> user);
}
