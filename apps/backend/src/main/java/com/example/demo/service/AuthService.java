package com.example.demo.service;

import com.example.demo.exception.InvalidLoginCredentialsException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.AppUserDetails;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found!"));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found!"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("user already exists!");
        }
        user.setUserId(UUID.randomUUID());
        userRepository.save(user);
    }

    public String authenticateAndGetToken(String email, String password) {
        User user = findUserByEmail(email);
        validatePassword(user, password);
        return generateTokenFromUser(user);
    }

    private void validatePassword(User user, String password) {
        if (!doesPasswordMatch(user, password)) {
            throw new InvalidLoginCredentialsException("Incorrect password!");
        }
    }

    private boolean doesPasswordMatch(User user, String plainTextPassword) {
        return passwordEncoder.matches(plainTextPassword, user.getPassword());
    }

    private String generateTokenFromUser(User user) {
        AppUserDetails userDetails = AppUserDetails.builder()
                .userId(user.getUserId())
//                .authorities()
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .is2faEnabled(user.isTwoFactorEnabled())
                .build();
        return jwtUtils.generateTokenFromUserDetails(userDetails);
    }
}