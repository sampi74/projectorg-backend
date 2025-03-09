package com.projectorg.controller;

import com.projectorg.dto.UserDto;
import com.projectorg.entities.Project;
import com.projectorg.entities.User;
import com.projectorg.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("projectorg/users")
public class UserController extends BaseController<User, Long> {
    private final UserService userService;

    public UserController(UserService userService) {
        super(userService);
        this.userService = userService;
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDto dto){
        User user = userService.updateUser(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/{id}/leading")
    public ResponseEntity<Set<Project>> getLeadingProjects(@PathVariable Long id){
        Set<Project> projects = userService.getAllUserLeadingProjects(id);
        return ResponseEntity.status(HttpStatus.OK).body(projects);
    }

    @GetMapping("/{id}/collaborating")
    public ResponseEntity<Set<Project>> getCollaboratingProjects(@PathVariable Long id){
        Set<Project> projects = userService.getAllUserCollaboratingProjects(id);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}/allprojects")
    public ResponseEntity<Set<Project>> getAllUserProjects(@PathVariable Long id){
        Set<Project> projects = Stream.concat(
                userService.getAllUserLeadingProjects(id).stream(),
                userService.getAllUserCollaboratingProjects(id).stream()
        ).collect(Collectors.toSet());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("query") String query) {
        List<User> users = userService.searchUsers(query);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(users);
    }

}
