package com.projectorg.controller;

import com.projectorg.dto.*;
import com.projectorg.entities.User;
import com.projectorg.security.JwtUtil;
import com.projectorg.service.UserDetailServiceImpl;
import com.projectorg.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("projectorg/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);
            User user = (User) userService.findByUsername(request.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return ResponseEntity.ok(new AuthResponse(jwt, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto dto) {
        userService.createUser(dto);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String token = request.getToken();
        String username = jwtUtil.extractUsername(token);

        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        if (jwtUtil.validateToken(token, userDetails)) {
            String newToken = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new RefreshTokenResponse(newToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
