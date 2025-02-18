package com.example.demo.controller;

import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.AppRole;
import com.example.demo.model.AppUserDetails;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.requests.LoginRequest;
import com.example.demo.requests.SignupRequest;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import com.example.demo.requests.JwtResponse;
import com.example.demo.model.User;

import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        User newUser = User.builder()
                .email(signupRequest.getEmail())
                .username(signupRequest.getUsername())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .accountNonLocked(true)
                .accountNonExpired(true)
                .enabled(true)
                .build();

        // Use Optional and simplify role assignment
        AppRole appRole = Optional.ofNullable(signupRequest.getRole())
                .map(role -> Arrays.stream(AppRole.values())
                        .filter(r -> r.name().equalsIgnoreCase(role))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Invalid role")))
                .orElse(AppRole.ROLE_USER); // Default role if no role is provided

        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Error: Role not found."));

        newUser.setRole(role);

        authService.registerUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }


    @PostMapping("/public/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String token = authService.authenticateAndGetToken(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/user-details")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal AppUserDetails userDetails) {
        User existedUser = Optional.ofNullable(userDetails.getEmail())
                .map(authService::findUserByEmail)
                .orElseThrow(() -> new UserNotFoundException("user not found"));


        return ResponseEntity.ok(existedUser);
    }

    @GetMapping("/csrf")
    public CsrfToken registerUser(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

}
