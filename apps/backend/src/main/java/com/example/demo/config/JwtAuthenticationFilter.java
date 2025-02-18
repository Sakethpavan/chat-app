package com.example.demo.config;

import java.io.IOException;

import com.example.demo.model.AppUserDetails;
import com.example.demo.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.util.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final ApplicationContext context;
    private final JwtUtils jwtUtils;


    public JwtAuthenticationFilter(ApplicationContext context, JwtUtils jwtUtils) {
        this.context = context;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtUtils.getJwtTokenFromHeader(request);

        if (token != null && jwtUtils.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtUtils.extractUserEmail(token);
            // find if user existing in db
            AppUserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByEmail(email);
            if (jwtUtils.validateToken(token, userDetails.getEmail())) {
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
