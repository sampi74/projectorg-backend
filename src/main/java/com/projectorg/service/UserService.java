package com.projectorg.service;

import com.projectorg.dto.UserDto;
import com.projectorg.entities.Project;
import com.projectorg.entities.ProjectCollaborator;
import com.projectorg.entities.User;
import com.projectorg.enumerations.UserRole;
import com.projectorg.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService extends BaseService<User, Long>{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public void createUser(UserDto dto){
        if (userRepository.existsByUserEmail(dto.getUserEmail())) {
            throw new IllegalArgumentException("The email " + dto.getUserEmail() + " is already in use.");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("The username " + dto.getUsername() + " is already in use.");
        }

        User user = new User();
        user.setUserEmail(dto.getUserEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserFullName(dto.getUserFullName());
        user.setUsername(dto.getUsername());
        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    public User createAdmin(UserDto dto){
        if (userRepository.existsByUserEmail(dto.getUserEmail())) {
            throw new IllegalArgumentException("The email " + dto.getUserEmail() + " is already in use.");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("The username " + dto.getUsername() + " is already in use.");
        }

        User user = new User();
        user.setUserEmail(dto.getUserEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserFullName(dto.getUserFullName());
        user.setUsername(dto.getUsername());
        user.setRole(UserRole.ADMIN);
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDto dto) {
        // 1. Buscar el usuario por ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        // 2. Validar si el nuevo nombre de usuario ya está en uso por otro usuario
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            boolean usernameExists = userRepository.existsByUsername(dto.getUsername());
            if (usernameExists) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(dto.getUsername());
        }
        // 3. Validar si el nuevo email ya está en uso por otro usuario
        if (dto.getUserEmail() != null && !dto.getUserEmail().equals(user.getUserEmail())) {
            boolean emailExists = userRepository.existsByUserEmail(dto.getUserEmail());
            if (emailExists) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setUserEmail(dto.getUserEmail());
        }
        // 4. Guardar cambios
        return userRepository.save(user);
    }

    public Set<Project> getAllUserLeadingProjects(Long id){
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getLedProjects();
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByUserEmailContainingOrUsernameContaining(query);
    }

    public Set<Project> getAllUserCollaboratingProjects(Long id) {
        // 1. Busco que el usuario exista
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        // 2. Busco los proyectos que colabora a traves de su clase intermedia
        Set<ProjectCollaborator> set = user.getProjectCollaborators();
        // 3. Armo un set con los proyectos
        Set<Project> projects = new HashSet<>();
        for (ProjectCollaborator p : set){
            projects.add(p.getProject());
        }

        return projects;
    }

    public Optional<Object> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByRole(String role) {
        UserRole userRole = UserRole.valueOf(role);
        return userRepository.findAllByRole(userRole);
    }
}
