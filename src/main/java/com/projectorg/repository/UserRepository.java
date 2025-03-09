package com.projectorg.repository;

import com.projectorg.entities.User;
import com.projectorg.enumerations.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByUserEmail(String email);
    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u WHERE LOWER(u.userEmail) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> findByUserEmailContainingOrUsernameContaining(String query);

    Optional<Object> findByUsername(String username);

    List<User> findAllByRole(UserRole userRole);
}
