package com.example.demo.service;


import com.example.demo.model.AppUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return AppUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .is2faEnabled(user.isTwoFactorEnabled())
                .build();
    }

    public AppUserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return AppUserDetails.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .password(user.getPassword())
                .is2faEnabled(user.isTwoFactorEnabled())
                .build();
    }
    
}
