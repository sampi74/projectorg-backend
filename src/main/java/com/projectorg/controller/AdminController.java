package com.projectorg.controller;

import com.projectorg.dto.UserDto;
import com.projectorg.entities.DiagramType;
import com.projectorg.entities.Project;
import com.projectorg.entities.TaskState;
import com.projectorg.entities.User;
import com.projectorg.service.DiagramTypeService;
import com.projectorg.service.ProjectService;
import com.projectorg.service.TaskStateService;
import com.projectorg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("projectorg/admin")
public class AdminController {

    private final UserService userService;
    private final TaskStateService taskStateService;
    private final DiagramTypeService diagramTypeService;
    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<User> createAdmin(@RequestBody UserDto dto){
        User user = userService.createAdmin(dto);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    // TaskState
    @PostMapping("/taskstate")
    public ResponseEntity<String> createTaskState(@RequestParam String name){
        taskStateService.create(name);
        return ResponseEntity.ok("Task State created");
    }

    @GetMapping("/taskstate")
    public ResponseEntity<?> getAllTaskState(){
        List<TaskState> states = taskStateService.getAll();

        if (states.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(states);
    }

    @GetMapping("/taskstate/{tsId}")
    public ResponseEntity<TaskState> getTaskState(@PathVariable Long tsId){
        TaskState taskState = taskStateService.get(tsId);
        return ResponseEntity.ok(taskState);
    }

    @GetMapping("/taskstate/low")
    public ResponseEntity<List<TaskState>> getAllTaskStatesLow(){
        List<TaskState> types = taskStateService.getAllLow();
        return ResponseEntity.ok(types);
    }

    @PutMapping("/taskstate/{tsId}")
    public ResponseEntity<String> updateTaskState(@PathVariable Long tsId, @RequestParam String name){
        taskStateService.update(tsId, name);
        return ResponseEntity.ok("Task State updated");
    }

    @GetMapping("/taskstate/{tsId}/count")
    public ResponseEntity<Integer> getTaskStateNumber(@PathVariable Long tsId){
        TaskState taskState = taskStateService.get(tsId);
        return ResponseEntity.ok(taskState.getTasks().size());
    }

    @DeleteMapping("/taskstate/{tsId}")
    public ResponseEntity<String> dropTaskState(@PathVariable Long tsId){
        taskStateService.drop(tsId);
        return ResponseEntity.ok("Task State dropped");
    }

    @PutMapping("/taskstate/{tsId}/discharge")
    public ResponseEntity<String> dischargeTaskState(@PathVariable Long tsId){
        taskStateService.discharge(tsId);
        return ResponseEntity.ok("Task State discharged");
    }

    // Diagram Type
    @PostMapping("/diagramtype")
    public ResponseEntity<String> createDiagramType(@RequestParam String name){
        diagramTypeService.create(name);
        return ResponseEntity.ok("Diagram Type created");
    }

    @GetMapping("/diagramtype")
    public ResponseEntity<List<DiagramType>> getAllDiagramTypes(){
        List<DiagramType> types = diagramTypeService.getAll();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/diagramtype/low")
    public ResponseEntity<List<DiagramType>> getAllDiagramTypesLow(){
        List<DiagramType> types = diagramTypeService.getAllLow();
        return ResponseEntity.ok(types);
    }

    @GetMapping("/diagramtype/{dtId}")
    public ResponseEntity<DiagramType> getDiagramType(@PathVariable Long dtId){
        DiagramType diagramType = diagramTypeService.get(dtId);
        return ResponseEntity.ok(diagramType);
    }

    @GetMapping("/diagramtype/{dtId}/count")
    public ResponseEntity<Integer> getDiagramTypeNumber(@PathVariable Long dtId){
        DiagramType diagramType = diagramTypeService.get(dtId);
        return ResponseEntity.ok(diagramType.getDiagrams().size());
    }

    @PutMapping("/diagramtype/{dtId}")
    public ResponseEntity<String> updateDiagramType(@PathVariable Long dtId, @RequestParam String name){
        diagramTypeService.update(dtId, name);
        return ResponseEntity.ok("Diagram Type updated");
    }

    @DeleteMapping("/diagramtype/{dtId}")
    public ResponseEntity<String> dropDiagramType(@PathVariable Long dtId){
        diagramTypeService.drop(dtId);
        return ResponseEntity.ok("Diagram Type dropped");
    }

    @PutMapping("/diagramtype/{dtId}/discharge")
    public ResponseEntity<String> dischargeDiagramType(@PathVariable Long dtId){
        diagramTypeService.discharge(dtId);
        return ResponseEntity.ok("Diagram Type discharged");
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getAllProjects(){
        List<Project> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Optional<Project>> getProjectById(@PathVariable Long projectId){
        Optional<Project> project = projectService.findById(projectId);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/projects/state")
    public ResponseEntity<List<Project>> getProjectsByState(@RequestParam String state){
        List<Project> projects = projectService.getProjectsByState(state);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/role")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role) {
        List<User> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}/leading")
    public ResponseEntity<Integer> getLeadingProjects(@PathVariable Long id){
        Integer cantidad = userService.getAllUserLeadingProjects(id).size();
        return ResponseEntity.ok(cantidad);
    }

    @GetMapping("/users/{id}/collaborating")
    public ResponseEntity<Integer> getCollaboratingProjects(@PathVariable Long id){
        Integer cantidad = userService.getAllUserCollaboratingProjects(id).size();
        return ResponseEntity.ok(cantidad);
    }
}
