package com.projectorg.controller;

import com.projectorg.dto.ProjectDto;
import com.projectorg.dto.TaskDto;
import com.projectorg.dto.UserDto;
import com.projectorg.entities.*;
import com.projectorg.service.ProjectService;
import com.projectorg.service.TaskStateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("projectorg/projects")
public class ProjectController extends BaseController<Project, Long>{
    private final ProjectService projectService;
    private final TaskStateService taskStateService;
    public ProjectController(ProjectService projectService, TaskStateService taskStateService) {
        super(projectService);
        this.projectService = projectService;
        this.taskStateService = taskStateService;
    }

    @PostMapping("/create")
    public ResponseEntity<Project> createProject(@RequestBody ProjectDto dto) {
        Project createdProject = projectService.createProject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody ProjectDto dto){
        Project project = projectService.updateProject(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(project);
    }

    @GetMapping("/{id}/ideas")
    public ResponseEntity<List<Idea>> getIdeas(@PathVariable Long id){
        List<Idea> ideas = projectService.getAllProjectIdeas(id);
        return ResponseEntity.status(HttpStatus.OK).body(ideas);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDto>> getTasks(@PathVariable Long id){
        List<TaskDto> tasks = projectService.getAllProjectTasks(id);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/{id}/requirements")
    public ResponseEntity<List<Requirement>> getRequirements(@PathVariable Long id){
        List<Requirement> requirements = projectService.getAllProjectRequirements(id);
        return ResponseEntity.status(HttpStatus.OK).body(requirements);
    }

    @PostMapping("/{projectId}/collaborators")
    public ResponseEntity<String> addCollaborator(
            @PathVariable Long projectId,
            @RequestParam Long userId,
            @RequestParam Long leaderId) {

        projectService.addCollaborator(projectId, userId, leaderId);
        return ResponseEntity.ok("Collaborator added successfully");
    }

    @PutMapping("/{projectId}/collaborators")
    public ResponseEntity<String> deleteCollaborator(
            @PathVariable Long projectId,
            @RequestParam Long userId,
            @RequestParam Long leaderId) throws AccessDeniedException {

        projectService.deleteCollaborator(projectId, userId, leaderId);
        return ResponseEntity.ok("Collaborator deleted");
    }

    @GetMapping("/{projectId}/collaborators")
    public ResponseEntity<Set<UserDto>> getCollaborators(@PathVariable Long projectId){

        Set<UserDto> dtos = projectService.getCollaborators(projectId);
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{projectId}/delete")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId, @RequestParam Long userId) throws AccessDeniedException {
        projectService.deleteProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{projectId}/state")
    public ResponseEntity<String> changeState(@PathVariable Long projectId, @RequestParam Long userId, @RequestParam String state) throws AccessDeniedException {
        projectService.changeState(projectId, userId, state);
        return ResponseEntity.ok("Project State changed to " + state);
    }

    @PostMapping("/{projectId}/ideas")
    public ResponseEntity<Idea> createIdea(@PathVariable Long projectId, @RequestParam Long userId, @RequestParam String text) throws AccessDeniedException {
        Idea idea = projectService.createIdea(projectId, userId, text);
        return ResponseEntity.status(HttpStatus.CREATED).body(idea);
    }

    @PutMapping("/{projectId}/ideas/{ideaId}")
    public ResponseEntity<String> editIdea(
            @PathVariable Long projectId,
            @PathVariable Long ideaId,
            @RequestParam Long userId,
            @RequestParam String newText) throws AccessDeniedException {

        projectService.editIdea(projectId, ideaId, userId, newText);
        return ResponseEntity.ok("Idea Updated");
    }

    @DeleteMapping("/{projectId}/ideas/{ideaId}")
    public ResponseEntity<String> deleteIdea(@PathVariable Long projectId, @PathVariable Long ideaId,
                                             @RequestParam Long userId) throws AccessDeniedException {
        projectService.deleteIdea(projectId, ideaId, userId);
        return ResponseEntity.ok("Idea eliminated");
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId, @RequestParam Long userId,
                                           @RequestParam String text) throws AccessDeniedException {
        Task task = projectService.createTask(projectId, userId, text);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<String> editTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @RequestParam(required = false) String newText,
            @RequestParam(required = false) String newState) {

        projectService.editTask(projectId, taskId, newText, newState);
        return ResponseEntity.ok("Task Updated");
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        projectService.deleteTask(projectId, taskId);
        return ResponseEntity.ok("Task eliminated");
    }

    @PostMapping("/{projectId}/requirements")
    public ResponseEntity<Requirement> createRequirement(@PathVariable Long projectId, @RequestParam Long userId,
                                                  @RequestParam String text, @RequestParam String type) throws AccessDeniedException {
        Requirement requirement = projectService.createRequirement(projectId, userId, text, type);
        return ResponseEntity.status(HttpStatus.CREATED).body(requirement);
    }

    @PutMapping("/{projectId}/requirements/{requirementId}")
    public ResponseEntity<String> editRequirement(
            @PathVariable Long projectId,
            @PathVariable Long requirementId,
            @RequestParam Long userId,
            @RequestParam(required = false) String newText,
            @RequestParam(required = false) String newType) throws AccessDeniedException {

        projectService.editRequirement(projectId, requirementId, userId, newText, newType);
        return ResponseEntity.ok("Task Updated");
    }

    @DeleteMapping("/{projectId}/requirements/{requirementId}")
    public ResponseEntity<String> deleteRequirement(@PathVariable Long projectId, @PathVariable Long requirementId,
                                                    @RequestParam Long userId) throws AccessDeniedException {
        projectService.deleteRequirement(projectId, requirementId, userId);
        return ResponseEntity.ok("Requirement eliminated");
    }

    @PostMapping("/{projectId}/files")
    public ResponseEntity<String> uploadFile(@PathVariable Long projectId, @RequestParam String name,
                                             @RequestParam Long userId, @RequestParam MultipartFile file) throws IOException {
        projectService.uploadFile(projectId, name, userId, file);
        return ResponseEntity.ok("File Uploaded");
    }

    @GetMapping("/{projectId}/files/{fileId}")
    public ResponseEntity<String> getFile(@PathVariable("projectId") Long projectId, @PathVariable("fileId") Long fileId){
        String url = projectService.getFileUrl(projectId, fileId);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{projectId}/files")
    public ResponseEntity<List<String>> getAllFiles(@PathVariable("projectId") Long projectId){
        List<String> urls = projectService.getAllFileUrl(projectId);
        return ResponseEntity.ok(urls);
    }

    @PutMapping("/{projectId}/files/{fileId}")
    public ResponseEntity<String> updateFile(
            @PathVariable("projectId") Long projectId,
            @PathVariable("fileId") Long fileId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("userId") Long userId) throws IOException {
        projectService.updateFile(projectId, fileId, name, file, userId);
        return ResponseEntity.ok("File Updated");
    }

    @DeleteMapping("/{projectId}/files/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable("projectId") Long projectId,
            @PathVariable("fileId") Long fileId,
            @RequestParam("userId") Long userId) throws AccessDeniedException {

        projectService.deleteFile(projectId, fileId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/diagrams")
    public ResponseEntity<String> uploadDiagram(@PathVariable Long projectId, @RequestParam Long userId,
                                                @RequestParam MultipartFile file, @RequestParam String type) throws IOException {
        projectService.uploadDiagram(projectId, userId, file, type);
        return ResponseEntity.ok("Diagram Uploaded");
    }

    @GetMapping("/{projectId}/diagrams/{diagramId}")
    public ResponseEntity<String> getDiagram(@PathVariable("projectId") Long projectId, @PathVariable("diagramId") Long fileId){
        String url = projectService.getDiagramUrl(projectId, fileId);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{projectId}/diagrams")
    public ResponseEntity<List<String>> getAllDiagramsUrl(@PathVariable("projectId") Long projectId){
        List<String> urls = projectService.getAllDiagramUrl(projectId);
        return ResponseEntity.ok(urls);
    }

    @PutMapping("/{projectId}/diagrams/{diagramId}")
    public ResponseEntity<String> updateFile(
            @PathVariable("projectId") Long projectId,
            @PathVariable("diagramId") Long diagramId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) throws IOException {
        projectService.updateDiagram(projectId, diagramId, file, userId);
        return ResponseEntity.ok("File Updated");
    }

    @DeleteMapping("/{projectId}/diagrams/{diagramId}")
    public ResponseEntity<Void> deleteDiagram(
            @PathVariable("projectId") Long projectId,
            @PathVariable("diagramId") Long diagramId,
            @RequestParam("userId") Long userId) throws AccessDeniedException {

        projectService.deleteDiagram(projectId, diagramId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/taskstate")
    public ResponseEntity<?> getAllTaskState(){
        List<TaskState> states = taskStateService.getAll();

        if (states.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(states);
    }

}
