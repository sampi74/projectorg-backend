package com.projectorg.service;

import com.projectorg.dto.ProjectDto;
import com.projectorg.dto.TaskDto;
import com.projectorg.dto.UserDto;
import com.projectorg.entities.*;
import com.projectorg.enumerations.ProjectState;
import com.projectorg.enumerations.RequirementType;
import com.projectorg.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.*;

@Service
public class ProjectService extends BaseService<Project, Long> {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectCollaboratorRepository projectCollaboratorRepository;
    private final IdeaRepository ideaRepository;
    private final TaskRepository taskRepository;
    private final TaskStateRepository taskStateRepository;
    private final RequirementRepository requirementRepository;
    private final AppWriteService appWriteService;
    private final FileRepository fileRepository;
    private final DiagramTypeRepository diagramTypeRepository;
    private final DiagramRepository diagramRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
                          ProjectCollaboratorRepository projectCollaboratorRepository,
                          IdeaRepository ideaRepository, TaskRepository taskRepository,
                          TaskStateRepository taskStateRepository, RequirementRepository requirementRepository,
                          AppWriteService appWriteService, FileRepository fileRepository,
                          DiagramTypeRepository diagramTypeRepository, DiagramRepository diagramRepository) {
        super(projectRepository);
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.projectCollaboratorRepository = projectCollaboratorRepository;
        this.ideaRepository = ideaRepository;
        this.taskRepository = taskRepository;
        this.taskStateRepository = taskStateRepository;
        this.requirementRepository = requirementRepository;
        this.appWriteService = appWriteService;
        this.fileRepository = fileRepository;
        this.diagramTypeRepository = diagramTypeRepository;
        this.diagramRepository = diagramRepository;
    }

    // metodo para crear un proyecto
    public Project createProject(ProjectDto dto) {
        User leader = userRepository.findById(dto.getLeaderId())
                .orElseThrow(() -> new EntityNotFoundException("Leader not found"));

        Project project = new Project();
        project.setProjectName(dto.getProjectName());
        project.setProjectDescription(dto.getProjectDescription());
        project.setProjectLeader(leader);
        project.setUpDateProject(LocalDate.now());
        project.setState(ProjectState.IN_PROGRESS);

        return repository.save(project);
    }

    // metodo para actualizar el proyecto
    public Project updateProject(Long id, ProjectDto dto){
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        if(dto.getProjectName() != null){
            project.setProjectName(dto.getProjectName());
        }
        if(dto.getProjectDescription() != null){
            project.setProjectDescription(dto.getProjectDescription());
        }
        return repository.save(project);
    }

    // metodo para ver todos los requisitos
    public List<Requirement> getAllProjectRequirements(Long id){
        Project project = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        return project.getRequirements();
    }

    // metodo para ver todas las ideas
    public List<Idea> getAllProjectIdeas(Long id){
        Project project = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        return project.getIdeas();
    }

    // metodo para ver todas las tareas
    public List<TaskDto> getAllProjectTasks(Long id){
        Project project = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        List<TaskDto> taskDtos = new ArrayList<>();
        for (Task task : project.getTasks()){
            TaskDto dto = new TaskDto();
            dto.setTaskId(task.getTaskId());
            dto.setTaskText(task.getTaskText());
            dto.setState(task.getState().getTaskStateId());
            taskDtos.add(dto);
        }
        return taskDtos;
    }

    // metodo para agregar colaborador a un proyecto
    @Transactional
    public void addCollaborator(Long projectId, Long userId, Long leaderId) {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        // 2. Verificar si el usuario que intenta agregar es el líder del proyecto
        if (!project.getProjectLeader().getUserId().equals(leaderId)) {
            throw new SecurityException("Only the project leader can add collaborators");
        }

        // 3. Buscar el usuario a agregar
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 4. Verificar si ya es colaborador
        if (projectCollaboratorRepository.existsByProjectAndUser(project, user)) {
            throw new IllegalArgumentException("User is already a collaborator of this project");
        }

        // 5. Crear la relación y guardarla
        ProjectCollaborator collaborator = new ProjectCollaborator();
        collaborator.setProject(project);
        collaborator.setUser(user);

        projectCollaboratorRepository.save(collaborator);
    }

    //metodo para eliminar colaborador de un proyecto
    @Transactional
    public void deleteCollaborator(Long projectId, Long userId, Long leaderId) throws AccessDeniedException {
        // 1. Buscar el proyecto en la base de datos
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario que intenta eliminar es el líder del proyecto
        if (!project.getProjectLeader().getUserId().equals(leaderId)) {
            throw new AccessDeniedException("Only the project leader can remove collaborators");
        }
        // 3. Buscar la relación del colaborador en la tabla intermedia
        ProjectCollaborator collaborator = projectCollaboratorRepository.findByProjectAndUser(project, userRepository.findById(userId))
                .orElseThrow(() -> new EntityNotFoundException("Collaborator not found in this project"));
        // 4. Eliminar la relación
        projectCollaboratorRepository.delete(collaborator);
    }

    // metodo para ver los colaboradores de un proyecto
    @Transactional
    public Set<UserDto> getCollaborators(Long projectId){
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Obtengo una lista con los colaboradores
        List<ProjectCollaborator> collaborators = projectCollaboratorRepository.findByProject(project);
        // 3. Creo un set de userdto y lo cargo con los datos de los colaboradores
        Set<UserDto> dtos = new HashSet<>();
        for (ProjectCollaborator collaborator : collaborators){
            User user = collaborator.getUser();
            UserDto dto = new UserDto(user.getUserFullName(), user.getUserEmail());
            dtos.add(dto);
        }
        // 4. Devuelvo la lista de dtos
        return dtos;
    }

    @Transactional
    public void deleteProject(Long projectId, Long userId) throws AccessDeniedException {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario es el líder del proyecto
        if (!project.getProjectLeader().getUserId().equals(userId)) {
            throw new AccessDeniedException("Only the project leader can delete this project");
        }
        // 3. Eliminar todas las relaciones con colaboradores
        projectCollaboratorRepository.deleteByProject(project);
        // 4. Eliminar el proyecto
        projectRepository.delete(project);
    }

    public void changeState(Long projectId, Long userId, String state) throws AccessDeniedException {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario es el líder del proyecto
        if (!project.getProjectLeader().getUserId().equals(userId)) {
            throw new AccessDeniedException("Only the project leader can change the state of this project");
        }
        // 3. Verifico que el estado ingresado sea valido
        try {
            ProjectState newState = ProjectState.valueOf(state.toUpperCase());
            project.setState(newState);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid project state: " + state);
        }
        // 5. Guardo el proyecto
        projectRepository.save(project);
    }

    @Transactional
    public Idea createIdea(Long projectId, Long userId, String text) throws AccessDeniedException {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can create an idea");
        }
        // 3. Crear y guardar la idea
        Idea idea = new Idea();
        idea.setIdeaText(text);
        idea.setProject(project);

        return ideaRepository.save(idea);
    }

    @Transactional
    public void editIdea(Long projectId, Long ideaId, Long userId, String newText) throws AccessDeniedException {
        // 1. Buscar la idea
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found"));
        // 2. Verificar que la idea pertenece al proyecto indicado
        if (!idea.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("The idea does not belong to the specified project");
        }
        // 3. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = idea.getProject().getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(idea.getProject(), userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can edit an idea");
        }
        // 4. Editar los campos de la idea
        idea.setIdeaText(newText);
        // 5. Guardar los cambios en la base de datos
        ideaRepository.save(idea);
    }

    @Transactional
    public void deleteIdea(Long projectId, Long ideaId, Long userId) throws AccessDeniedException {
        // 1. Buscar la idea en la base de datos
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Idea not found"));
        // 2. Verificar que la idea pertenece al proyecto indicado
        if (!idea.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("The idea does not belong to the specified project");
        }
        // 3. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = idea.getProject().getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(idea.getProject(), userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can eliminate an idea");
        }
        // 4. Eliminar la idea
        ideaRepository.delete(idea);
    }

    public Task createTask(Long projectId, Long userId, String text) throws AccessDeniedException {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can create a task");
        }
        // 3. Creo y guardo la tarea
        Task task = new Task();
        task.setProject(project);
        task.setTaskText(text);
        TaskState state = taskStateRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("TaskState not found"));
        task.setState(state);

        return taskRepository.save(task);
    }

    public void editTask(Long projectId, Long taskId, String newText, String newState) {
        // 1. Buscar la idea
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        // 2. Verificar que la idea pertenece al proyecto indicado
        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("The task does not belong to the specified project");
        }
        // 3. Editar los campos de la tarea
        if (newText != null && !newText.isBlank()){
            task.setTaskText(newText);
        }
        // 4. Si se proporciona un estado, buscarlo en la base de datos y asignarlo
        if (newState != null && !newState.isBlank()) {
            TaskState state = taskStateRepository.findByTaskStateName(newState);
            task.setState(state);
        }
        // 5. Guardar los cambios en la base de datos
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long projectId, Long taskId) {
        // 1. Buscar la tarea en la base de datos
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        // 2. Verificar que la tarea pertenece al proyecto indicado
        if (!task.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("The task does not belong to the specified project");
        }
        // 3. Eliminar la tarea
        taskRepository.delete(task);
    }

    public Requirement createRequirement(Long projectId, Long userId, String text, String type) throws AccessDeniedException {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can create an idea");
        }
        // 3. Creo y guardo el requerimiento
        Requirement requirement = new Requirement();
        requirement.setProject(project);
        requirement.setRequirementText(text);
        try {
            RequirementType newtype = RequirementType.valueOf(type.toUpperCase());
            requirement.setRequirementType(newtype);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid requirement type: " + type);
        }

        return requirementRepository.save(requirement);
    }

    public void editRequirement(Long projectId, Long requirementId, Long userId, String newText, String newType) throws AccessDeniedException {
        // 1. Buscar la idea
        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));
        // 2. Verificar que la idea pertenece al proyecto indicado
        if (!requirement.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("The requirement does not belong to the specified project");
        }
        // 3. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = requirement.getProject().getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(requirement.getProject(), userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can create an idea");
        }
        // 4. Editar los campos de el requerimiento
        if (newText != null && !newText.isBlank()){
            requirement.setRequirementText(newText);
        }
        // 5. Si se proporciona un tipo, buscarlo en la base de datos y asignarlo
        if (newType != null && !newType.isBlank()) {
            try {
                RequirementType type = RequirementType.valueOf(newType.toUpperCase());
                requirement.setRequirementType(type);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid requirement type: " + newType);
            }
        }
        // 6. Guardar los cambios en la base de datos
        requirementRepository.save(requirement);
    }

    @Transactional
    public void deleteRequirement(Long projectId, Long requirementId, Long userId) throws AccessDeniedException {
        // 1. Buscar el requerimiento en la base de datos
        Requirement requirement = requirementRepository.findById(requirementId)
                .orElseThrow(() -> new EntityNotFoundException("Requirement not found"));
        // 2. Verificar que el requerimiento pertenece al proyecto indicado
        if (!requirement.getProject().getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("The requirement does not belong to the specified project");
        }
        // 3. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = requirement.getProject().getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(requirement.getProject(), userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can create an idea");
        }
        // 4. Eliminar el requerimiento
        requirementRepository.delete(requirement);
    }

    public void uploadFile(Long projectId, String name, Long userId, MultipartFile file) throws IOException {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can create a file");
        }
        // 3. Subir el archivo a la api
        name = name.toLowerCase().replace(" ", "_");
        byte[] fileData = file.getBytes();
        String fileName = projectId + "_" + name;
        String fileUrl = appWriteService.uploadFile(fileData, fileName);
        // 4.Crear la entidad File
        File file1 = new File();
        file1.setProject(project);
        file1.setFileName(fileName);
        file1.setFileImageUrl(fileUrl);
        // 5. Guardar entidad
        fileRepository.save(file1);
    }

    public String getFileUrl(Long projectId, Long fileId) {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Buscar el archivo
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));
        // 3. Devolver la url del archivo
        return file.getFileImageUrl();
    }

    public List<String> getAllFileUrl(Long projectId) {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Crear lista de urls
        List<String> urls = new ArrayList<>();
        // 3. Buscar los archivos
        for (File file : project.getFiles()){
            urls.add(file.getFileImageUrl());
        }
        // 4. Devolver lista
        return urls;
    }

    public void updateFile(Long projectId, Long fileId, String name, MultipartFile file, Long userId) throws IOException {
        // 1. Buscar el archivo
        File existingFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));
        if (!existingFile.getProject().getProjectId().equals(projectId)) {
            throw new AccessDeniedException("The file does not belong to this project");
        }
        // 2. Verificar si el usuario es el líder o un colaborador
        Project project = existingFile.getProject();
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can update a file");
        }
        // 3. Actualizar nombre si se envió
        if (name != null && !name.isBlank()) {
            name = name.toLowerCase().replace(" ", "_");
            existingFile.setFileName(projectId + "_" + name);
        }
        // 4. Subir nueva imagen si se envió
        if (file != null && !file.isEmpty()) {
            appWriteService.deleteFile(existingFile.getFileImageUrl());
            String newFileUrl = appWriteService.uploadFile(file.getBytes(), existingFile.getFileName());
            existingFile.setFileImageUrl(newFileUrl);
        }
        // 5. Guardar cambios en la BD
        fileRepository.save(existingFile);
    }

    public void deleteFile(Long projectId, Long fileId, Long userId) throws AccessDeniedException {
        // 1. Buscar el archivo
        File existingFile = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));
        if (!existingFile.getProject().getProjectId().equals(projectId)) {
            throw new AccessDeniedException("The file does not belong to this project");
        }
        // 2. Verificar si el usuario es el líder o un colaborador
        Project project = existingFile.getProject();
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can delete a file");
        }
        // 3. Eliminar de AppWrite
        appWriteService.deleteFile(existingFile.getFileImageUrl());
        // 4. Eliminar de la base de datos
        fileRepository.delete(existingFile);
    }

    public void uploadDiagram(Long projectId, Long userId, MultipartFile file, String type) throws IOException {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Verificar si el usuario es el líder o un colaborador
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can create a diagram");
        }
        // 3. Crear entidad Diagrama
        Diagram diagram = new Diagram();
        diagram.setProject(project);
        try {
            DiagramType diagramType = diagramTypeRepository.findByDiagramTypeName(type);
            diagram.setDiagramType(diagramType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid diagram type: " + type);
        }
        // 4. Subir el archivo a la api
        String name = type.toLowerCase().replace(" ", "_");
        byte[] fileData = file.getBytes();
        String fileName = projectId + "_" + name;
        String fileUrl = appWriteService.uploadDiagram(fileData, fileName);
        // 5. Asignar url y guardar entidad
        diagram.setDiagramImageUrl(fileUrl);
        diagramRepository.save(diagram);
    }

    public String getDiagramUrl(Long projectId, Long diagramId) {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Buscar el diagrama
        Diagram diagram = diagramRepository.findById(diagramId)
                .orElseThrow(() -> new EntityNotFoundException("Diagram not found"));
        // 3. Devolver la url del archivo
        return diagram.getDiagramImageUrl();
    }

    public List<String> getAllDiagramUrl(Long projectId) {
        // 1. Buscar el proyecto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        // 2. Crear lista de urls
        List<String> urls = new ArrayList<>();
        // 3. Buscar los diagramas
        for (Diagram diagram : project.getDiagrams()){
            urls.add(diagram.getDiagramImageUrl());
        }
        // 4. Devolver lista
        return urls;
    }

    public void updateDiagram(Long projectId, Long diagramId, MultipartFile file, Long userId) throws IOException {
        // 1. Buscar el diagrama
        Diagram existingDiagram = diagramRepository.findById(diagramId)
                .orElseThrow(() -> new EntityNotFoundException("Diagram not found"));
        if (!existingDiagram.getProject().getProjectId().equals(projectId)) {
            throw new AccessDeniedException("The diagram does not belong to this project");
        }
        // 2. Verificar si el usuario es el líder o un colaborador
        Project project = existingDiagram.getProject();
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can update a diagram");
        }
        // 3. Subir nueva imagen
        String name = existingDiagram.getDiagramType().getDiagramTypeName().toLowerCase().replace(" ", "_");
        appWriteService.deleteFile(existingDiagram.getDiagramImageUrl());
        String newFileUrl = appWriteService.uploadFile(file.getBytes(), name);
        existingDiagram.setDiagramImageUrl(newFileUrl);
        // 4. Guardar cambios en la BD
        diagramRepository.save(existingDiagram);
    }

    public void deleteDiagram(Long projectId, Long diagramId, Long userId) throws AccessDeniedException {
        // 1. Buscar el diagrama
        Diagram existingDiagram = diagramRepository.findById(diagramId)
                .orElseThrow(() -> new EntityNotFoundException("Diagram not found"));
        if (!existingDiagram.getProject().getProjectId().equals(projectId)) {
            throw new AccessDeniedException("The Diagram does not belong to this project");
        }
        // 2. Verificar si el usuario es el líder o un colaborador
        Project project = existingDiagram.getProject();
        boolean isLeader = project.getProjectLeader().getUserId().equals(userId);
        boolean isCollaborator = projectCollaboratorRepository.existsByProjectAndUser(project, userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found")));

        if (!isLeader && !isCollaborator) {
            throw new AccessDeniedException("Only the project leader or a collaborator can delete a diagram");
        }
        // 3. Eliminar de AppWrite
        appWriteService.deleteFile(existingDiagram.getDiagramImageUrl());
        // 4. Eliminar de la base de datos
        diagramRepository.delete(existingDiagram);
    }

    public List<Project> getProjectsByState(String state) {
        ProjectState projectState = ProjectState.valueOf(state);
        return projectRepository.findAllByState(projectState);
    }
}
